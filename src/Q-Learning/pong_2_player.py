from math import floor
from numpy import random
from numpy import sign
import numpy as np
import math
import pprint
import pickle
import time


class Pong:
    def __init__(self, grid_length, is_paddle1):
        self.paddle_height = 0.2        # Height of paddle in continuous domain
        self.grid_length = grid_length  # Grid length for approximating continuous game as discrete
        self.paddle_speed = 0.04        # Speed at which the paddle moves up or down
        self.is_paddle1 = is_paddle1      # Checks whether the first paddle is a player or a wall

    def calculate_discrete_state(self, c_state):
        # Grab the continuous state parameters to calculate the discrete state
        ball_x = c_state[0]
        ball_y = c_state[1]
        velocity_x = c_state[2]
        velocity_y = c_state[3]
        paddle_y = c_state[4]
        p1_paddle_y = c_state[5]

        # Calculate grid coordinates (values 0 through (grid_length - 1))
        d_ball_x = floor(self.grid_length * ball_x)
        d_ball_y = floor(self.grid_length * ball_y)

        # Calculate discrete velocities (values -1, 0, or 1)
        d_velocity_x = sign(velocity_x)
        d_velocity_y = sign(velocity_y)
        if abs(velocity_y) < 0.015:
            d_velocity_y = 0

        # Calculate paddle position (values 0 through (grid_length - 1))
        d_paddle_y = floor(self.grid_length * paddle_y / (1 - self.paddle_height))
        if paddle_y == 1-self.paddle_height: ################################
            d_paddle_y = 11
            
        d_p1_paddle_y = floor(self.grid_length * p1_paddle_y / (1 - self.paddle_height))
        if p1_paddle_y == 1-self.paddle_height: ################################
            d_p1_paddle_y = 11

        # Status that tells if the ball passed the right paddle
        ball_passed = ball_x > 1

        state = (d_ball_x, d_ball_y, d_velocity_x, d_velocity_y, d_paddle_y,d_p1_paddle_y)

        return state, ball_passed

    def time_step(self, c_state, action, paddle1_y):
        # Grab the continuous state parameters to calculate the next continuous state
        ball_x = c_state[0]
        ball_y = c_state[1]
        velocity_x = c_state[2]
        velocity_y = c_state[3]
        paddle_y = c_state[4]
        p1_paddle_y = c_state[5]
        bounced = 0
        if action is None:
            action =0

        # Increment ball position and paddle position. Action is 0 for nothing, -1 for move up, and 1 for move down
        ball_x = ball_x + velocity_x
        ball_y = ball_y + velocity_y
        paddle_y = paddle_y + action*self.paddle_speed
        if ball_y > p1_paddle_y + self.paddle_height / 2:
            paddle1_action = 1
        elif ball_y < p1_paddle_y + self.paddle_height / 2:
            paddle1_action = -1
        else:
            paddle1_action = 0
        p1_paddle_y = p1_paddle_y + paddle1_action*self.paddle_speed/2


    # Check the bounds of paddle 1
        if p1_paddle_y > 1 - self.paddle_height:
            p1_paddle_y = 1 - self.paddle_height
        if p1_paddle_y < 0:
            p1_paddle_y = 0

        # Restricts paddle to be within screen
        if paddle_y > 1 - self.paddle_height:
            paddle_y = 1 - self.paddle_height
        if paddle_y < 0:
            paddle_y = 0

        # Case where ball hits the top of the screen
        if ball_y < 0:
            ball_y = -1*ball_y
            velocity_y = -1*velocity_y

        # Case where ball hits the bottom of the screen
        if ball_y > 1:
            ball_y = 2 - ball_y
            velocity_y = -1*velocity_y

        # Case where ball hits the left edge of the screen or paddle 1
        if self.is_paddle1:
            if (ball_x < 0) and (ball_y >= paddle1_y) and (ball_y <= paddle1_y + self.paddle_height):
                ball_x = -1*ball_x

                # Have the velocity slightly randomized after paddle bounce
                u = random.uniform(-0.015, 0.015)
                v = random.uniform(-0.03, 0.03)
                velocity_x = -1 * velocity_x + u
                velocity_y = velocity_y + v

                # Ensure that speed of x direction is at least 0.03
                if abs(velocity_x) < 0.03:
                    velocity_x = sign(velocity_x) * 0.03
        else:
            if ball_x < 0:
                ball_x = -1*ball_x
                velocity_x = -1*velocity_x

        # Case where ball hits the right paddle
        if (ball_x > 1) and (ball_y >= paddle_y) and (ball_y <= paddle_y + self.paddle_height) or ((ball_x < 0) and (ball_y <= p1_paddle_y) and (ball_y >= p1_paddle_y + self.paddle_height)):
            ball_x = 2 - ball_x

            # Have the velocity slightly randomized after paddle bounce
            u = random.uniform(-0.015, 0.015)
            v = random.uniform(-0.03, 0.03)
            velocity_x = -1*velocity_x + u
            velocity_y = velocity_y + v

            # Ensure that speed of x direction is at least 0.03
            if abs(velocity_x) < 0.03:
                velocity_x = sign(velocity_x)*0.03
            bounced = 1

        # Update the state
        next_c_state = (ball_x, ball_y, velocity_x, velocity_y, paddle_y,bounced,p1_paddle_y)

        return next_c_state

    def predict_next_state(self, state, action, c_state):
        # Initialize the next states
        next_state = state
        next_c_state = c_state
        reward = 0

        # Keep stepping in time until the discrete state changes
        while next_state == state:
            next_c_state = self.time_step(next_c_state, action,0)
            next_state, ball_passed = self.calculate_discrete_state(next_c_state)
        if next_state[2] == -1 and next_state[0] == 11: ###########################
            reward = 1
        if ball_passed:
            reward = -1          ###########################

        return next_state, next_c_state,reward

    def get_winner(self, c_state):
        ball_x = c_state[0]
        # Paddle 2 Wins
        if ball_x < 0:
            winner = 2
        # Paddle 2 Wins
        elif ball_x > 1:
            winner = 1
        # No winner yet
        else:
            winner = 0

        return winner



    
    
    
    
    
    
    

    
    
def next_action(d_state):
    best_action = 0
    max_q = -math.inf
    action_array = [-1,0,1]
    random.shuffle(action_array)

    # If state action pair freq is under Ne, try it
    for action_choice in action_array:
        if (d_state,action_choice) in freq_table and freq_table[(d_state,action_choice)] < Ne:
            return action_choice

    # Else find the best next action
    for action_choice in range (-1,2):
        if (d_state,action_choice) in Q_table and Q_table[(d_state,action_choice)] > max_q:
            max_q = Q_table[(d_state,action_choice)]
            best_action = action_choice

    return best_action

def next_max_Q(disc_state,action,c_state):
    (next_state,next_c_state,reward) = p.predict_next_state(disc_state,action,c_state)
    max_next_Q = -math.inf

    if reward == -1:
        max_next_Q = -1

    for a in range(-1,2):
        if (next_state,a) in Q_table and Q_table[(next_state,a)] > max_next_Q:
            max_next_Q = Q_table[(next_state,a)]
    return max_next_Q
    
def Q_learning(d_state,action,reward,c_state):
    if reward == -1 :
        Q_table[(d_state, action)] = reward
        return 0
    elif (d_state,action) in freq_table:
        freq_table[(d_state,action)] += 1
        old_Q = Q_table[(d_state,action)]

        alpha = C/(C+freq_table[(d_state,action)])
        Q_table[(d_state,action)] = old_Q + alpha*(reward+(gamma*next_max_Q(d_state,action,c_state))-old_Q)

        next_a = next_action(d_state)
        return next_a
    
    

    
C = 30
gamma = 0.7
Ne = 8

Q_table = {}
freq_table = {}
for x in range(12):
    for y in range(12):
        for x_velo in range(-1,2):
            if x_velo != 0:
                for y_velo in range(-1,2):
                    for paddle_leng in range(12):
                        for p1 in range(12):
                            for act in range (-1,2):
                                state = (x,y,x_velo,y_velo,paddle_leng,p1)
                                Q_table[(state,act)]=0
                                freq_table[(state,act)]=0


                            

timer = 0
count = 0
t = time.time()
p = Pong(12,0)
c_state_init = (0.5,0.5, 0.5, 0.03, 0.01, 0.5 - p.paddle_height / 2)
d_state_init, ball_passed = p.calculate_discrete_state(c_state_init)
while (timer <100000):

    # Action to choose at start of game
    action = next_action(d_state_init)

    # Initialize start c and d states
    c_state = c_state_init
    d_state = d_state_init

    # Reward is 0 regardless of action at start of game
    reward = 0

    # Keep iterating until the game is over
    while reward != -1:
        # Update the state
        (d_state,c_state,reward) = p.predict_next_state(d_state,action,c_state)

        # Learn, then make the next action
        action = Q_learning(d_state, action, reward, c_state)

        if reward == 1:
            count+=1

    timer+=1
print(timer)
print(count)
print("learning done")
timer = 0
count = 0
while (timer<1000):
    # Action to choose at start of game
    action = next_action(d_state_init)

    # Initialize start c and d states
    c_state = c_state_init
    d_state = d_state_init

    # Reward is 0 regardless of action at start of game
    reward = 0

    # Keep iterating until the game is over
    while reward != -1:
        # Update the state
        (d_state, c_state, reward) = p.predict_next_state(d_state, action, c_state)

        # Learn, then make the next action
        if reward != -1:
            action = next_action(d_state)
        if reward == 1:
            count += 1

    timer += 1
print(count/timer)
#pprint.pprint(Q_table)
print((time.time() - t))
output = open('Q_table.txt', 'wb')
pickle.dump(Q_table, output)
output.close()