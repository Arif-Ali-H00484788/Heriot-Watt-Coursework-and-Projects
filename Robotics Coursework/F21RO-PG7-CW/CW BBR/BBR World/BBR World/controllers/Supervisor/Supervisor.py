from controller import Supervisor, Receiver
import random
import math
import csv
import time

class PrizeSupervisor:
    def __init__(self):
        self.supervisor = Supervisor()
        self.distance_threshold = 0.06 # Touching the prize threshold
        self.receiver = self.supervisor.getDevice('receiver') # Getting and enabling receiver to receive messages from controller
        self.receiver.setChannel(1)
        self.receiver.enable(32)
        self.iteration_durations = [] # Iteration duration
        self.total_start_time = time.time()
        print('Total simulation start time recorded.')
        self.iteration_start_time = None
        self.iteration_count = 0
        self.reset() # Calling parameters which must reset at each iteration
        
    def reset(self):
        # Resetting supervisor parameters at each iteration
        self.supervisor.simulationResetPhysics() # Resetting physics of the simulation at each iteration to flush any trace physical properties from previous simulation
        print('Supervisor is resetting')
        
        # Prize positions
        self.prize_positions = [
            [-0.36, 0.02, -0.17],  # Left side position (T-Maze A)
            [0.36, 0.02, -0.17]    # Right side position (T-Maze B)
        ]
        
        # Initializing robot, spotlight, and prize nodes
        self.prize_node = self.supervisor.getFromDef('Prize')
        self.epuck_node = self.supervisor.getFromDef('Controller')
        self.light_node = self.supervisor.getFromDef('Light')
        
        self.set_initial_light_state() 
        self.iteration_start_time = time.time()
        print(f'Iteration {self.iteration_count + 1} start time recorded.')

    def set_initial_light_state(self):
        # Setting the spotlight status in the beginning of each iteration
        light_on = True
        # we are keeping the light off for 1st 3 iterations, and on for rest three.
        if self.iteration_count < 3:
            light_on = False
        self.light_node.getField('on').setSFBool(light_on)
        # Prize placed at the right arm if light is on, otherwise placed left
        prize_position = self.prize_positions[1] if light_on else self.prize_positions[0]
        self.prize_node.getField('translation').setSFVec3f(prize_position)
        
        print(f'Initial light state set to: {'on' if light_on else 'off'}')
        print(f'Prize placed at: {'right' if light_on else 'left'} side of the maze.')

    def set_epuck_initial_position(self):
        # Setting the epuck robot at its original starting position and rotation after each iteration
        position = [0.00896207, -6.27552e-05, 0.354759]
        rotation = [-0.5798828092942618, 0.5762048105038432, 0.5759548105860606, 2.08919]
        self.epuck_node.getField('translation').setSFVec3f(position)
        self.epuck_node.getField('rotation').setSFRotation(rotation)
        print('e-puck reset to initial position.')

    def check_proximity(self):
        # Check the proximity of the robot to the prize and disappearing the prize if there is contact
        epuck_position = self.epuck_node.getField('translation').getSFVec3f()
        prize_position = self.prize_node.getField('translation').getSFVec3f()
        
        distance = math.sqrt(
            (epuck_position[0] - prize_position[0]) ** 2 +
            (epuck_position[1] - prize_position[1]) ** 2 +
            (epuck_position[2] - prize_position[2]) ** 2
        )
        
        if distance < self.distance_threshold: 
            print('e-puck touched the prize!')
            self.disappear_prize()
        
        # Receiving the end message from controller to go to the next iteration
        if self.receiver.getQueueLength() > 0:
            message = self.receiver.getBytes()
            if message == b'E':
                print('End message received from controller')
                self.handle_simulation_end()
            self.receiver.nextPacket()

    def handle_simulation_end(self):
        # Handling end of the whole simulation
        if self.iteration_start_time is not None:
            iteration_duration = time.time() - self.iteration_start_time
            t_maze_name = 'T-Maze B' if self.light_node.getField('on').getSFBool() else 'T-Maze A'
            self.iteration_durations.append((t_maze_name, iteration_duration))
            self.iteration_count += 1
            print(f'Recorded {t_maze_name} duration: {iteration_duration:.2f} seconds')
            print(f'Current iteration durations list: {self.iteration_durations}')

        if self.iteration_count < 6:
            print(f'{6 - self.iteration_count} iterations remaining until CSV generation.')
            self.set_epuck_initial_position()
            self.reset()
        else:
            print('Six iterations complete. Writing to CSV.')
            total_duration = time.time() - self.total_start_time
            self.write_to_csv(total_duration)

    def write_to_csv(self, total_duration):
        # CSV generation at the end of simulation
        try:
            with open('t_maze_iterations.csv', mode='w', newline='') as file:
                writer = csv.writer(file)
                writer.writerow(['Maze Name', 'Duration(s)'])
                writer.writerows(self.iteration_durations)
                writer.writerow([f'Total Duration: {total_duration}(s)'])
            print("CSV file 't_maze_iterations.csv' generated successfully.")
        except Exception as e:
            print(f'Error writing to CSV: {e}')

    def disappear_prize(self):
        # Prize relocated out of view in a distant location in the world once robot "finds" it, i.e end of each iteration
        self.prize_node.getField('translation').setSFVec3f([1000, 1000, 1000])
        print('Prize disappeared')

    def run(self):
        while self.supervisor.step(32) != -1:
            self.check_proximity()


if __name__ == "__main__":
    supervisor = PrizeSupervisor()
    supervisor.run()