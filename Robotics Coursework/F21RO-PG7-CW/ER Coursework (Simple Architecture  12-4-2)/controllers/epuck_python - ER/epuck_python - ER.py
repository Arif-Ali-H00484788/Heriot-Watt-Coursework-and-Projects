from controller import Robot, Receiver, Emitter
import sys,struct,math
import numpy as np
import mlp as ntw

class Controller:
    
    #  Initialize the controller class with the robot object and its parameters #  
    
    def __init__(self, robot):        
        # Robot Parameters
        # Please, do not change these parameters
        self.robot = robot
        self.time_step = 32 # ms
        self.max_speed = 1  # m/s
 
        # MLP Parameters and Variables 
        #DEFINE below the architecture of your MLP network:
         
        # Add the number of neurons for input layer, hidden layer and output layer.
        # The number of neurons should be in between of 1 to 20.
        # Number of hidden layers should be one or two.
        # Example with one hidden layers: self.number_hidden_layer = [5]
        # Example with two hidden layers: self.number_hidden_layer = [7,5]        
        
        self.number_input_layer = 12
        self.number_hidden_layer = [4]
        self.number_output_layer = 2
        
        # Create a list with the number of neurons per layer
        self.number_neuros_per_layer = []
        self.number_neuros_per_layer.append(self.number_input_layer)
        self.number_neuros_per_layer.extend(self.number_hidden_layer)
        self.number_neuros_per_layer.append(self.number_output_layer)
        
        # Initialize the network
        self.network = ntw.MLP(self.number_neuros_per_layer)
        self.inputs = []
        
        # Calculate the number of weights of your MLP
        self.number_weights = 0
        for n in range(1,len(self.number_neuros_per_layer)):
            if(n == 1):
                # Input + bias
                self.number_weights += (self.number_neuros_per_layer[n-1]+1)*self.number_neuros_per_layer[n]
            else:
                self.number_weights += self.number_neuros_per_layer[n-1]*self.number_neuros_per_layer[n]

        # Enable Motors
        self.left_motor = self.robot.getDevice('left wheel motor')
        self.right_motor = self.robot.getDevice('right wheel motor')
        self.left_motor.setPosition(float('inf'))
        self.right_motor.setPosition(float('inf'))
        self.left_motor.setVelocity(0.0)
        self.right_motor.setVelocity(0.0)
        self.velocity_left = 0
        self.velocity_right = 0
    
        # Enable Proximity Sensors
        self.proximity_sensors = []
        for i in range(8):
            sensor_name = 'ps' + str(i)
            self.proximity_sensors.append(self.robot.getDevice(sensor_name))
            self.proximity_sensors[i].enable(self.time_step)
        
        # Enable Light Sensors    
        self.light_sensors = []
        for i in range(4):
            sensor_name = 'ls' + str(i)
            self.light_sensors.append(self.robot.getDevice(sensor_name))
            self.light_sensors[i].enable(self.time_step)
              
        # Enable Emitter and Receiver (to communicate with the Supervisor)
        self.emitter = self.robot.getDevice("emitter") 
        self.receiver = self.robot.getDevice("receiver") 
        self.receiver.enable(self.time_step)
        self.receivedData = "" 
        self.receivedDataPrevious = "" 
        self.flagMessage = False
        
        #Junction Metrics
        self.has_reached_junction = False
        self.light_beam_detected = False
        self.has_made_decision = False
        self.turning_decision = 0       # 0 for Left and 1 for Right
        
        #Spin metrics
        self.previous_velocity_left = 0
        self.previous_velocity_right = 0
        
        # Fitness value (initialization fitness parameters once)
        self.fitness_values = []
        self.fitness = 0
        

#-----------------------------------Starter Code Functions Start----------------------------------------------#

    def check_for_new_genes(self):
        # Check if there is a new genotype to be used that was sent from Supervisor
        # If there is a new genotype, then update the weights of the network
        if(self.flagMessage == True):
                # Split the list based on the number of layers of your network
                part = []
                for n in range(1,len(self.number_neuros_per_layer)):
                    if(n == 1):
                        part.append((self.number_neuros_per_layer[n-1]+1)*(self.number_neuros_per_layer[n]))
                    else:   
                        part.append(self.number_neuros_per_layer[n-1]*self.number_neuros_per_layer[n])
                
                # Set the weights of the network
                data = []
                weightsPart = []
                sum = 0
                for n in range(1,len(self.number_neuros_per_layer)):
                    if(n == 1):
                        weightsPart.append(self.receivedData[n-1:part[n-1]])
                    elif(n == (len(self.number_neuros_per_layer)-1)):
                        weightsPart.append(self.receivedData[sum:])
                    else:
                        weightsPart.append(self.receivedData[sum:sum+part[n-1]])
                    sum += part[n-1]
                for n in range(1,len(self.number_neuros_per_layer)):  
                    if(n == 1):
                        weightsPart[n-1] = weightsPart[n-1].reshape([self.number_neuros_per_layer[n-1]+1,self.number_neuros_per_layer[n]])    
                    else:
                        weightsPart[n-1] = weightsPart[n-1].reshape([self.number_neuros_per_layer[n-1],self.number_neuros_per_layer[n]])    
                    data.append(weightsPart[n-1])                
                self.network.weights = data
                
                #Reset fitness list
                self.fitness_values = []
    
    
    def clip_value(self,value,min_max):   
        if (value > min_max):
            return min_max;
        elif (value < -min_max):
            return -min_max;
        return value;

    def sense_compute_and_actuate(self):
        # MLP: 
        #   Input == sensory data
        #   Output == motors commands
        #   Hidden == number of hidden layers and neurons
        #   Weights == number of weights
        #   Activation function == Tanh
        #   Propagation == forward
        
        output = self.network.propagate_forward(self.inputs)
        self.velocity_left = output[0]
        self.velocity_right = output[1]
        
        # Multiply the motor values by 2 to increase the velocities
        self.left_motor.setVelocity(self.velocity_left*2)
        self.right_motor.setVelocity(self.velocity_right*2)
        
    def handle_emitter(self):
        # Send the self.fitness value to the supervisor
        # Send the number of weights to the supervisor
        # Check if there are messages to be sent to our Supervisor
        # Send the fitness value to the supervisor
        data = str(self.number_weights)
        data = "weights: " + data
        string_message = str(data)
        string_message = string_message.encode("utf-8")
        #print("Robot send:", string_message)
        self.emitter.send(string_message)

        # Send the self.fitness value to the supervisor
        data = str(self.fitness)
        data = "fitness: " + data
        string_message = str(data)
        string_message = string_message.encode("utf-8")
        #print("Robot send fitness:", string_message)
        self.emitter.send(string_message)
            
    def handle_receiver(self):
        if self.receiver.getQueueLength() > 0:
            while(self.receiver.getQueueLength() > 0):
                # Adjust the Data to our model
                #Webots 2022:
                #self.receivedData = self.receiver.getData().decode("utf-8")
                #Webots 2023:
                self.receivedData = self.receiver.getString()
                self.receivedData = self.receivedData[1:-1]
                self.receivedData = self.receivedData.split()
                x = np.array(self.receivedData)
                self.receivedData = x.astype(float)
                #print("Controller handle receiver data:", self.receivedData)
                self.receiver.nextPacket()
                
            # Is it a new Genotype?
            if(np.array_equal(self.receivedDataPrevious,self.receivedData) == False):
                self.flagMessage = True
                
            else:
                self.flagMessage = False
                
            self.receivedDataPrevious = self.receivedData 
        else:
            #print("Controller receiver q is empty")
            self.flagMessage = False

#---------------------------------------------------------------Starter Code Functions End--------------------------------------------------------------------------#

#----------------------------------------------------------Implemented Fitness Functions Start----------------------------------------------------------------------#

    def detect_light_beam(self):
        
        # Light beam detection focusing on right side sensors
        
        right_sensors = [0, 1, 2 , 3]  # Right-side sensor indices
        light_count = 0                # Number of sensors detecting light
        light_threshold = 0.02442        # Light threshold value
        
        # Get light sensor values
        light_values = [self.light_sensors[i].getValue() for i in right_sensors]
        #print("Light Sensor Values: ", light_values)
        
        # Check if light is detected by the sensors
        for i in light_values:
            if i >= 0 and i < light_threshold :
                light_count += 1
        # print("Light Count: ", light_count)
        
        # If at least 2 sensors detect light, then light beam is detected
        if light_count >= 1:
            self.light_beam_detected = True
        else:
            self.light_beam_detected = False
    
    def forward_behaviour(self):
        # Initialize variables
        overall_movement_score = 0
        forward_movement_score = 0
        forward_fitness = 0
        
        # Path deviation boundary conditions for better forward motion
        front_path_deviation_threshold = 0.03663
        left_side_sum = 0
        right_side_sum = 0
        rear_path_deviation_threshold = 0.03663
        
        # Encourage forward motion of the robot
        avg_forward_speed = (self.velocity_left + self.velocity_right) / 2
        #print("Average Forward Speed: ", avg_forward_speed)
        if avg_forward_speed > 0:
            forward_movement_score = 1
        else:
            forward_movement_score = 0
            
        # If the robot is moving forward, then ensure that it is moving in a straight line in the center of the path
        if forward_movement_score > 0:
            overall_movement_score += 1         # Encourage the robot to move forward
            # Get sensor values for path deviation calculation (in pairs)              
            front_sensors = [self.proximity_sensors[0].getValue(), self.proximity_sensors[7].getValue()]
            side_sensors_left = [self.proximity_sensors[5].getValue() , self.proximity_sensors[6].getValue()]
            side_sensors_right = [self.proximity_sensors[1].getValue() , self.proximity_sensors[2].getValue()]
            rear_sensors= [self.proximity_sensors[3].getValue() , self.proximity_sensors[4].getValue()]
            
            # Calculate path deviation based on sensor values
            left_side_sum = ( side_sensors_left[0] + side_sensors_left[1])
            right_side_sum = ( side_sensors_right[0] + side_sensors_right[1])
            
            front_path_deviation = (front_sensors[0] + front_sensors[1])
            rear_path_deviation = (rear_sensors[0] + rear_sensors[1])
            
            side_deviation = ((left_side_sum-right_side_sum)/ (left_side_sum + right_side_sum))
                       
            
            # Check if the robot is moving in a straight line in the center of the path
            if front_path_deviation <= front_path_deviation_threshold and rear_path_deviation <= rear_path_deviation_threshold and side_deviation <= 0.1 and side_deviation >= 0 :
                overall_movement_score += 1   # Encourage the robot to move in a straight line in the center of the path
            else:
                overall_movement_score -= 1   # Penalize the robot for not moving in a straight line in the center of the path
        else:
            overall_movement_score -= 5 # Penalize the robot for not moving forward
        
        #print("Movement Score: ", movement_score)
        #print("Forward Movement Score: ", forward_movement_score)
        #print("Overall Movement Score: ", overall_movement_score)
        #print("Forward Fitness: ", forward_fitness)
        
        #print ("Front Path Deviation: ", front_path_deviation)
        #print ("Rear Path Deviation: ", rear_path_deviation)
        #print ("Side Path Deviation: ", side_deviation)
        
        # Calculate the forward fitness based on the movement score
        forward_fitness = overall_movement_score * forward_movement_score
        return forward_fitness
    
    def avoid_collision_zones(self):
        # Define safety zones
        safe_zone = 0.0854       # Values below this are completely safe
        warning_zone = 0.2442   # Values below this are in warning zone
        danger_zone = 0.5116    # Values above warning_zone are in danger zone
        
        # Group sensors by direction
        front_sensors = [self.proximity_sensors[0].getValue(), self.proximity_sensors[7].getValue()]
        left_sensors = [self.proximity_sensors[5].getValue(), self.proximity_sensors[6].getValue()]
        right_sensors = [self.proximity_sensors[1].getValue(), self.proximity_sensors[2].getValue()]
        rear_sensors = [self.proximity_sensors[3].getValue(), self.proximity_sensors[4].getValue()]
        
        # Calculate normalized sensor values
        front_value = max(front_sensors)
        left_value = max(left_sensors) 
        right_value = max(right_sensors) 
        rear_value = max(rear_sensors) 
        
        # Initialize collision avoidance fitness
        collision_fitness = 0
        front_fitness = 0
        left_side_fitness = 0
        right_side_fitness = 0
        rear_fitness = 0
        
        # Assess front collision risk based on sensor values
        if front_value <= safe_zone:
            front_fitness += 5.0  # Safe zone
        elif front_value < warning_zone and front_value > safe_zone:
            # Penalize proportionally in warning zone
            front_fitness -= (front_value * 2.5)
        else:
            front_fitness -= 5.0  # Danger zone
        
        # Assess side collision risks
        # Penalize the robot for being too close to the walls
        # Left side
        if left_value <= safe_zone:
            left_side_fitness += 5.0
        elif left_value <= warning_zone and left_value > safe_zone:
            # Partial penalty if one side is in warning zone
            left_side_fitness-= (left_value * 2.5)
        else:
            left_side_fitness -= 5.0  # Sides in danger
        
        # Right side
        if right_value <= safe_zone:
            right_side_fitness += 5.0
        elif right_value <= warning_zone and right_value > safe_zone:
            right_side_fitness -= (right_value * 2.5)
        else:
            right_side_fitness -= 5.0
            
        # Assess rear collision risk
        if rear_value <= safe_zone:
            rear_fitness += 5.0
        elif rear_value <= warning_zone and rear_value > safe_zone:
            rear_fitness -= (rear_value * 5)
        else:
            rear_fitness-= 5.0
    
        # Combine fitness components
        collision_fitness = ((front_fitness + left_side_fitness + right_side_fitness + rear_fitness) / 4) * 2.5
        # Return scaled fitness
        return collision_fitness
    
    def avoid_spinning_behaviour(self):

        max_velocity = max(self.velocity_left, self.velocity_right)
        min_velocity = min(self.velocity_left, self.velocity_right)

        movement_efficiency = (1 -((max_velocity - min_velocity) + ((max_velocity + min_velocity) / 2)))
       
        # Combined fitness score
        spinning_fitness = movement_efficiency
        return spinning_fitness        
        
    def detect_junction(self):
        junction_decision = 0
        junction_decision_fitness = 0
        # Junction detection based on front, side and rear proximity sensors
        # Get front proximity values and bounsdary condition
        front_threshold = 0.13431
        front_sum=0
        
        # Get side proximity values and  boundary condition
        side_threshold = 0.03663
        left_side_sum=0
        right_side_sum=0
        
        # Get rear proximity values and boundary condition
        rear_threshold = 0.13431
        rear_sum=0
        
        # Get sensor values
        front_sensors = [self.proximity_sensors[0].getValue(), self.proximity_sensors[7].getValue()]
        left_side_sensors = [self.proximity_sensors[5].getValue() , self.proximity_sensors[6].getValue()]
        right_side_sensors = [self.proximity_sensors[1].getValue() , self.proximity_sensors[2].getValue()]
        rear_sensors= [self.proximity_sensors[3].getValue() , self.proximity_sensors[4].getValue()]
        
        # Junction conditions:
        # 1. Front wall detected (high front sensor values)
        # 2. At least one side is clear (low side sensor values)
        for i in front_sensors:
            front_sum+=i                       # Sum of front sensor values
        for i in left_side_sensors:
            left_side_sum+=i                   # Sum of left side sensor values
        for i in right_side_sensors:
            right_side_sum+=i                  # Sum of right side sensor values
        for i in rear_sensors:
            rear_sum+=i                        # Sum of rear sensor values
        
        if front_sum <= front_threshold and left_side_sum <=side_threshold and right_side_sum <=side_threshold and rear_sum <=rear_threshold:
            # Junction is detected           
            self.has_reached_junction = True                        # Junction is reached
            junction_decision = self.junction_decision()            # Make a decision at the junction
            junction_decision_fitness = junction_decision*2.5       # Increase the fitness value for making a decision
            return junction_decision_fitness                        # Return the fitness value
            #print("Junction Detected")
        
        else:
            self.has_reached_junction = False                      # Junction is not reached
            junction_decision_fitness = -5                       # Penalize the robot for not making a decision
            return junction_decision_fitness                       # Return the fitness value
            #print("Junction Not Detected")
            
    def junction_decision(self):
        # Junction decision making based on light beam detection and junction detection
        # If light beam is detected and junction is reached, then make a decision
        decision_fitness = 0
        if self.light_beam_detected and self.has_reached_junction:
            self.has_made_decision = True
            self.turning_decision = 1  # Turn Right
            decision_fitness = 5
            return decision_fitness
        elif self.has_reached_junction and not self.light_beam_detected:
            self.has_made_decision = True
            self.turning_decision = 0 # Turn Left
            decision_fitness = 5
            return decision_fitness
        else:
            self.has_made_decision = False
            self.turning_decision = 0 # Always turn Left by default
            decision_fitness = -5   # Penalize the robot for not making a decision
            return decision_fitness   
           
    def calculate_fitness(self):
        
        # Call the fitness function to increase the speed of the robot and to encourage the robot to move forward
        ForwardFitness = self.forward_behaviour()
        JunctionFitness = self.detect_junction()              
        
        # Call the fitness function equation to avoid collision
        AvoidCollisionFitness = self.avoid_collision_zones()      
       
        # Call the fitness function equation to avoid spining behaviour
        SpinningFitness = self.avoid_spinning_behaviour()
        
        # DEFINE the fitness function equation of this iteration which should be a combination of the previous functions         
        combinedFitness = (  0.23 * ForwardFitness +  0.23 * JunctionFitness+ + 0.27 * SpinningFitness + 0.27 * AvoidCollisionFitness ) 
        # Adjust the weights of the fitness functions to balance the fitness value of the robot
        #print("Combined Fitness: ", combinedFitness)
        
        # Append the fitness value to the list
        self.fitness_values.append(combinedFitness)
        self.fitness = np.mean(self.fitness_values)
        #print("Fitness of current poulation: ", self.fitness)
        
#-----------------------------------------------------------Implemented Fitness Functions End-----------------------------------------------------------------------#

#---------------------------------------------------------------------------Main Loop-------------------------------------------------------------------------------#

    def run_robot(self):        
        # Main Loop
        while self.robot.step(self.time_step) != -1:
            # This is used to store the current input data from the sensors
            self.inputs = []
            # Emitter and Receiver
            # Check if there are messages to be sent or read to/from our Supervisor
            self.handle_emitter()
            self.handle_receiver()
            
            # Read Light Sensors
            for i in range(4):
                ### Select the distance sensors that you will use      
                    temp = self.light_sensors[i].getValue()
                    
                    ### Please adjust the distance sensors values to facilitate learning 
                    min_ls = 0
                    max_ls = 4095
                    
                    if(temp > max_ls): temp = max_ls
                    if(temp < min_ls): temp = min_ls
                    
                    # Normalize the values between 0 and 1 and save data
                    self.inputs.append((temp-min_ls)/(max_ls-min_ls))
                    #print("Distance Sensors - Index: {}  Value: {}".format(i,self.proximity_sensors[i].getValue()))
            
            # Read Distance Sensors
            for i in range(8):
                ### Select the distance sensors that you will use        
                    temp = self.proximity_sensors[i].getValue()
                    
                    ### Please adjust the distance sensors values to facilitate learning 
                    min_ds = 0
                    max_ds = 4095
                    
                    if(temp > max_ds): temp = max_ds
                    if(temp < min_ds): temp = min_ds
                    
                    # Normalize the values between 0 and 1 and save data
                    self.inputs.append((temp-min_ds)/(max_ds-min_ds))
                    #print("Distance Sensors - Index: {}  Value: {}".format(i,self.proximity_sensors[i].getValue()))
    
            # GA Iteration       
            # Verify if there is a new genotype to be used that was sent from Supervisor  
            self.check_for_new_genes()
            # The robot's actuation (motor values) based on the output of the MLP 
            self.sense_compute_and_actuate()
            # Calculate the fitnes value of the current iteration
            self.calculate_fitness()
            
            # Update previous velocities for spinning penalty calculations
            self.previous_left_velocity = self.velocity_left
            self.previous_right_velocity = self.velocity_right
            
            # End of the iteration 
            
if __name__ == "__main__":
    # Call Robot function to initialize the robot
    my_robot = Robot()
    # Initialize the parameters of the controller by sending my_robot
    controller = Controller(my_robot)
    # Run the controller
    controller.run_robot()
    
#-----------------------------------------------------------------------End of Main Loop--------------------------------------------------------------------------------#