from math import floor
from numpy import random
from numpy import sign
import numpy as np
import math

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
            d_paddle_y = self.grid_length - 1

        # Status that tells if the ball passed the right paddle
        ball_passed = ball_x > 1

        state = (d_ball_x, d_ball_y, d_velocity_x, d_velocity_y, d_paddle_y)

        return state, ball_passed

    def time_step(self, c_state, action, paddle1_y):
        # Grab the continuous state parameters to calculate the next continuous state
        ball_x = c_state[0]
        ball_y = c_state[1]
        velocity_x = c_state[2]
        velocity_y = c_state[3]
        paddle_y = c_state[4]
        bounced = 0

        # Increment ball position and paddle position. Action is 0 for nothing, -1 for move up, and 1 for move down
        ball_x = ball_x + velocity_x
        ball_y = ball_y + velocity_y
        paddle_y = paddle_y + action*self.paddle_speed

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

        # Case where velocity exceeds 1

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
        if (ball_x > 1) and (ball_y >= paddle_y) and (ball_y <= paddle_y + self.paddle_height):
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
        next_c_state = (ball_x, ball_y, velocity_x, velocity_y, paddle_y,bounced)

        return next_c_state

    def predict_next_state(self, state, action, c_state):
        # Initialize the next states
        next_state = state
        next_c_state = c_state
        reward = 0
        ball_passed = 0
        # Keep stepping in time until the discrete state changes
        while next_state == state:
            next_c_state = self.time_step(next_c_state, action,0)
            next_state, ball_passed = self.calculate_discrete_state(next_c_state)
        if next_state[2] == -1 and next_state[0] == self.grid_length-1:
            reward = 1
        if ball_passed:
            reward = -1

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
