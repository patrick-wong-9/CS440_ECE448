from pong import Pong
import qlearn
import pickle
import time

# Global Variables
new_Q = 1
NUM_TRAIN_GAMES = 100000
grid_length = 12

def param_init():
    if(new_Q): # Construct new Q and freq tables
        Q_table = {}
        freq_table = {}
        for x in range(grid_length):
            for y in range(grid_length):
                for x_velo in range(-1, 2):
                    if x_velo != 0:
                        for y_velo in range(-1, 2):
                            for paddle_leng in range(grid_length):
                                for act in range(-1, 2):
                                    state = (x, y, x_velo, y_velo, paddle_leng)
                                    Q_table[(state, act)] = 0
                                    freq_table[(state, act)] = 0
    else:
        # Load the Tables for learning
        output = open('P2_Q_table.txt', 'rb')
        Q_table = pickle.load(output)
        output = open('P2_freq_table.txt', 'rb')
        freq_table = pickle.load(output)

    return Q_table, freq_table

def train(Q_table, freq_table):
    #Keep track of current game and the count of the bounces
    timer = 0
    bounce_count = 0

    p = Pong(grid_length, 0)
    c_state_init = (0.5, 0.5, 0.03, 0.01, 0.5 - p.paddle_height / 2)
    d_state_init, ball_passed = p.calculate_discrete_state(c_state_init)
    while (timer < NUM_TRAIN_GAMES):

        # Action to choose at start of game
        action = qlearn.next_action(d_state_init, Q_table, freq_table)

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
            action = qlearn.Q_learning(Q_table, freq_table, d_state, action, reward, c_state)

            if reward == 1:
                bounce_count += 1

        timer += 1
    print(timer)
    print(bounce_count)
    print("learning done")
    return Q_table, freq_table

def test(Q_table, freq_table):
    timer = 0
    bounce_count = 0

    p = Pong(grid_length, 0)
    c_state_init = (0.5, 0.5, 0.03, 0.01, 0.5 - p.paddle_height / 2)
    d_state_init, ball_passed = p.calculate_discrete_state(c_state_init)

    while (timer < 10000):
        # Action to choose at start of game
        action = qlearn.next_action(d_state_init, Q_table, freq_table)

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
                action = qlearn.next_action(d_state, Q_table, freq_table)
            if reward == 1:
                bounce_count += 1

        timer += 1
    print(bounce_count / timer)

def main():
    # Initialize the Q table and freq table
    Q_table, freq_table = param_init()

    # Train and time it
    start = time.clock()
    Q_table, freq_table = train(Q_table, freq_table)
    print(time.clock() - start)

    # test the learner
    test(Q_table, freq_table)

    # Save the output tables
    output = open('10_Q_table.txt', 'wb')
    pickle.dump(Q_table, output)
    output.close()
    output = open('10_freq_table.txt', 'wb')
    pickle.dump(freq_table, output)
    output.close()

# Test the algorithm
if __name__ == "__main__":
    main()
