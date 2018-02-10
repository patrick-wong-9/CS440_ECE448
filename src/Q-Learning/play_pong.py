# play_pong
# Link for installing Pygame: https://stackoverflow.com/questions/41405001/pygame-installation-for-python-3-6-0

import pygame
import sys
from pong import Pong
from math import floor
import pickle
import qlearn

# PADDLE 1 PLAYER:
# 0 for wall
# 1 for pt2.2 player
# 2 for controllable player
PADDLE1_PLAYER = 2

# Pong Globals
GRID_LENGTH = 12
C_PAD_HEIGHT = 0.2
C_PAD1_SPEED = 0.02
C_PAD2_SPEED = 0.04
paddle1_action = 0
paddle2_action = 0
paddle1_y = 0.5 - C_PAD_HEIGHT / 2

# GUI globals
WHITE = (255, 255, 255)
ORANGE = (255, 165, 0)
BLUE = (0, 51, 102)  # Colors
SIDE = 300
BALL_RADIUS = 5
PAD_WIDTH = 3
PAD_HEIGHT = floor(SIDE * C_PAD_HEIGHT)
PAD1_X = 0
PAD2_X = SIDE - 1


# Resets global game variables
def reset():
    global paddle1_y, paddle1_action, paddle2_action
    paddle1_action = 0
    paddle2_action = 0
    paddle1_y = 0.5 - C_PAD_HEIGHT / 2


# Function to update paddle 1 position depending on the PADDLE1_PLAYER type
def update_paddle1_pos(ball_y):
    global paddle1_action, paddle1_y

    # Set the player as a slower paddle that tracks ball_y
    if PADDLE1_PLAYER == 1:
        if ball_y > paddle1_y + C_PAD_HEIGHT / 2:
            paddle1_action = 1
        elif ball_y < paddle1_y + C_PAD_HEIGHT / 2:
            paddle1_action = -1
        else:
            paddle1_action = 0
        paddle1_y = paddle1_y + paddle1_action * C_PAD1_SPEED

    # Set the player to be controllable and moves as fast as paddle 2
    if PADDLE1_PLAYER == 2:
        paddle1_y = paddle1_y + paddle1_action * C_PAD2_SPEED

    # Check the bounds of paddle 1
    if paddle1_y > 1 - C_PAD_HEIGHT:
        paddle1_y = 1 - C_PAD_HEIGHT
    if paddle1_y < 0:
        paddle1_y = 0


# draw function of canvas
def draw(canvas, c_state):
    global paddle1_y

    # Get ball and Agent's positions from the continous state
    ball_x = c_state[0]
    ball_y = c_state[1]
    paddle2_y = c_state[4]

    # Calculate paddle 1 position
    update_paddle1_pos(ball_y)

    # Calculate GUI coordinates
    g_ball_x = floor(SIDE * ball_x)
    g_ball_y = floor(SIDE * ball_y)
    g_paddle1_y = floor(SIDE * paddle1_y)
    g_paddle2_y = floor(SIDE * paddle2_y)

    # Draw background
    canvas.fill(BLUE)

    # Draw ball
    pygame.draw.circle(canvas, WHITE, [g_ball_x, g_ball_y], BALL_RADIUS, 0)

    # Draw paddle 1 or wall
    if PADDLE1_PLAYER == 0:
        pygame.draw.polygon(canvas, ORANGE, [[PAD1_X + PAD_WIDTH, 0],
                                             [PAD1_X + PAD_WIDTH, SIDE],
                                             [PAD1_X, SIDE],
                                             [PAD1_X, 0]], 0)
    else:
        pygame.draw.polygon(canvas, ORANGE, [[PAD1_X + PAD_WIDTH, g_paddle1_y],
                                             [PAD1_X + PAD_WIDTH, g_paddle1_y + PAD_HEIGHT],
                                             [PAD1_X, g_paddle1_y + PAD_HEIGHT],
                                             [PAD1_X, g_paddle1_y]], 0)

    # Draw paddle 2
    pygame.draw.polygon(canvas, ORANGE, [[PAD2_X - PAD_WIDTH, g_paddle2_y],
                                         [PAD2_X - PAD_WIDTH, g_paddle2_y + PAD_HEIGHT],
                                         [PAD2_X, g_paddle2_y + PAD_HEIGHT],
                                         [PAD2_X, g_paddle2_y]], 0)


# Handle paddle action upon key press
def key_down(event):
    global paddle1_action, paddle2_action

    if event.key == pygame.K_UP:
        if PADDLE1_PLAYER == 2:
            paddle1_action = -1
    elif event.key == pygame.K_DOWN:
        if PADDLE1_PLAYER == 2:
            paddle1_action = 1


# Handle paddle action upon key release
def key_up():
    global paddle1_action, paddle2_action
    if PADDLE1_PLAYER == 2:
        paddle1_action = 0


def main():
    global paddle2_action, paddle1_y

    # Initialize Game
    pygame.init()
    fps = pygame.time.Clock()
    screen = pygame.display.set_mode((SIDE, SIDE), 0, 32)
    pygame.display.set_caption('Pong')

    # Create Pong class and initialize the continuous state
    png = Pong(GRID_LENGTH, PADDLE1_PLAYER)
    init_c_state = (0.5, 0.5, 0.03, 0.01, 0.5 - C_PAD_HEIGHT / 2)
    c_state = init_c_state
    reset()  # Initializes global variables

    # Load the Tables for learning
    output = open('P2_Q_table.txt', 'rb')
    Q_table = pickle.load(output)
    output = open('P2_freq_table.txt', 'rb')
    freq_table = pickle.load(output)

    # game loop
    while 1:
        # Draw the current state on the screen
        draw(screen, c_state)

        # Check for key presses
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                key_down(event)
            elif event.type == pygame.KEYUP:
                key_up()
            elif event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()

        # Update the continuous state and check for a winner
        c_state = png.time_step(c_state, paddle2_action, paddle1_y)
        if png.get_winner(c_state):
            reset()
            c_state = init_c_state  # Reset game upon winning

        d_state, ball_passed = png.calculate_discrete_state(c_state)
        paddle2_action = qlearn.next_action(d_state, Q_table, freq_table)
        # Update the game's display
        pygame.display.update()
        fps.tick(10)


# Run the game
if __name__ == "__main__":
    main()

