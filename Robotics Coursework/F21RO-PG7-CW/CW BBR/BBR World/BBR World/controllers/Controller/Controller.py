from controller import Robot, Emitter

class Controller:
    def __init__(self, robot):        
        # Robot Parameters
        self.robot = robot
        self.time_step = 32  # ms
        self.max_speed = 5.5  # m/s
        self.adjustment_factor = 0.05 # Centering mechanism adjustment factor
        self.error_tolerance = 5 # Tolerance of proximity sensors comparison
        self.turning_threshold = 200 # Turning left or right threshold
        self.max_motor_velocity = 6.28
        self.end_count = 0 # Number of iterations count
        self.wall_threshold = 150 # Walls threshold
        self.equality_tolerance = 10 # Frontal proximity sensors equality tolerance
        self.near_wall_sensors_threshold = 115 # Proximity sensor threshold for sensors near walls during end detection
        self.away_from_wall_sensors_threshold = 70 # Proximity sensor threshold for sensors away from walls during end detection
        self.reset() # Calling parameters which must reset at each iteration
        
    def reset(self):
        # Resetting Parameters at each iteration
        # Enable Motors
        self.left_motor = self.robot.getDevice('left wheel motor')
        self.right_motor = self.robot.getDevice('right wheel motor')
        self.emitter = self.robot.getDevice('emitter')
        self.emitter.setChannel(1)
        self.left_motor.setPosition(float('inf'))
        self.right_motor.setPosition(float('inf'))
        self.left_motor.setVelocity(0.0)
        self.right_motor.setVelocity(0.0)
    
        # Enable Proximity Sensors
        self.proximity_sensors = [self.robot.getDevice(f'ps{i}') for i in range(8)]
        for sensor in self.proximity_sensors:
            sensor.enable(self.time_step)
        # Enable light Sensors
        self.light_sensors = [self.robot.getDevice(f'ls{i}') for i in range(8)]
        for sensor in self.light_sensors:
            sensor.enable(self.time_step)

        # Robot mechanism triggers to react to different scenarios
        self.light_trigger = False # Detecting spotlight from right side of the robot
        self.end_trigger = False # Detecting the end (happens when robot is surrounded with three walls on its front, left, and right sides) 
        self.turn_trigger = False # Robot turns at junctions based on detection of spotlight
        
    
    def print_sensor_and_motor_values(self):
        # Gather light sensor values with names
        light_sensor_values = {f'ls{i}': self.light_sensors[i].getValue() for i in range(len(self.light_sensors))}
        # Gather proximity sensor values with names
        proximity_sensor_values = {f'ps{i}': self.proximity_sensors[i].getValue() for i in range(len(self.proximity_sensors))}
        # Get motor values
        left_motor_value = self.left_motor.getVelocity()
        right_motor_value = self.right_motor.getVelocity()

        # Print the light sensor values with names
        print('Light Sensor Values:')
        for name, value in light_sensor_values.items():
            print(f'  {name}: {value}')

        # Print the proximity sensor values with names
        print('Proximity Sensor Values:')
        for name, value in proximity_sensor_values.items():
            print(f'  {name}: {value}')

        # Print the motor speeds
        print('Motor Speeds:')
        print(f'  Left Motor: {left_motor_value}')
        print(f'  Right Motor: {right_motor_value}')
        
    def forward(self):
        # Set both motor at same velocity inorder to move forward
        self.left_motor.setVelocity(self.max_speed)
        self.right_motor.setVelocity(self.max_speed)

    def stop(self):
        # Set both motor at velocity at speed 0 inorder to stop the robot
        self.left_motor.setVelocity(0.0)
        self.right_motor.setVelocity(0.0)
    
    def motor_flush(self):
        # Flushing motors
        self.left_motor.setVelocity(0.0)
        self.right_motor.setVelocity(0.0)
        
    def light_is_found(self):
        # Getting the value of the light sensor placed on the right side
        ls3_value = self.light_sensors[3].getValue()
        if not self.light_trigger: 
            # The value is 0 when light is detected
            if ls3_value == 0:
                self.light_trigger = True
                print('Light detected')
                # Emit light signal to supervisor
                self.emitter.send(b'L')

    def clamp_velocity(self, velocity):
        # Velocity Clamping
        return max(-self.max_motor_velocity, min(self.max_motor_velocity, velocity))

    def center_between_walls(self):
        # Robot motion logic
        if not self.end_trigger:
            ps_values = [sensor.getValue() for sensor in self.proximity_sensors]
            # Making sure all the proximity sensors are near the walls except the ones behind to detect the end
            if (all(ps_values[i] > self.near_wall_sensors_threshold for i in [0, 1, 2, 5, 6, 7]) and
                all(ps_values[i] < self.away_from_wall_sensors_threshold for i in [3, 4])):  # End condition
                self.end_trigger = True
                print('End detected!')
                self.stop()  
                # Emit end signal to supervisor
                self.emitter.send(b'E')
                return  
                

        
        if not self.end_trigger:
            # Junction turn logic based on the detection of light
            if self.turn_trigger:
                if self.light_trigger:
                    self.left_motor.setVelocity(0.3)
                    self.right_motor.setVelocity(-0.3)
                    if self.left_side > self.turning_threshold:
                        print('Turning right complete')
                        self.forward()
                        self.turn_trigger = False
                elif not self.light_trigger:
                    self.left_motor.setVelocity(-0.3)
                    self.right_motor.setVelocity(0.3)
                    if self.right_side > self.turning_threshold:
                        print('Turning left complete')
                        self.forward()
                        self.turn_trigger = False
                return  

            # Comparing proximity sensors on the left with sensors on the right for the centering mechanism
            right_avg = sum(self.proximity_sensors[i].getValue() for i in range(4)) / 4
            left_avg = sum(self.proximity_sensors[i].getValue() for i in range(4, 8)) / 4
            error = left_avg - right_avg

            # keep moving forward without hitting walls (centering) until the robot reaches a junction
            if not self.end_trigger:  
                if abs(error) > self.error_tolerance:  
                    half_error = error / 2
                    if half_error < 0: 
                        left_speed = self.max_speed - self.adjustment_factor * abs(half_error)
                        right_speed = self.max_speed + self.adjustment_factor * abs(half_error)
                        self.left_motor.setVelocity(self.clamp_velocity(left_speed))
                        self.right_motor.setVelocity(self.clamp_velocity(right_speed))
                        print('Moving left')
                    elif half_error > 0:  
                        left_speed = self.max_speed + self.adjustment_factor * abs(half_error)
                        right_speed = self.max_speed - self.adjustment_factor * abs(half_error)
                        self.left_motor.setVelocity(self.clamp_velocity(left_speed))
                        self.right_motor.setVelocity(self.clamp_velocity(right_speed))
                        print('Moving right')
                else:
                    # Detecting frontal wall
                    if (self.front_right > self.wall_threshold and 
                        self.front_left > self.wall_threshold and 
                        abs(self.front_right - self.front_left) < self.equality_tolerance):
                        print('Frontal wall detected')
                        self.turn_trigger = True
                    else:  
                        self.forward()
                        print('Robot is centered')


    def sense_and_actuate(self):
        # Read proximity sensor values
        self.front_right = self.proximity_sensors[0].getValue() 
        self.front_left = self.proximity_sensors[7].getValue() 
        self.upper_right = self.proximity_sensors[1].getValue()
        self.upper_left = self.proximity_sensors[6].getValue()
        self.right_side = self.proximity_sensors[2].getValue() 
        self.left_side = self.proximity_sensors[5].getValue() 
        self.back_right = self.proximity_sensors[3].getValue()
        self.back_left = self.proximity_sensors[4].getValue()
        self.light_is_found()
        self.left_motor_value = self.left_motor.getVelocity()
        self.right_motor_value = self.right_motor.getVelocity()
        
        
        # Calling robot motion logic
        self.center_between_walls()
        # Print sensor and motor values
        self.print_sensor_and_motor_values()

    def run_robot(self):
        # keep running this loop until end of 6 iterations
        while self.robot.step(self.time_step) != -1:
            if self.end_trigger:
                if self.left_motor_value == 0 and self.right_motor_value == 0:
                    self.end_count += 1
                    print('Robot has reached the end.')
                    if self.end_count < 6:
                        self.reset()
                        print('Robot is resetting')
                        self.motor_flush()
                        continue
                    else:
                        break
            self.sense_and_actuate()

if __name__ == "__main__":
    my_robot = Robot()
    controller = Controller(my_robot)
    controller.run_robot()