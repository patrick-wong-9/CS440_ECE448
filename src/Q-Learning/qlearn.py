from numpy import random
import math
from pong import Pong


C = 30
gamma = 0.7
Ne = 8

def next_action(d_state, Q_table, freq_table):
    best_action = 0
    max_q = -math.inf
    action_array = [-1, 0, 1]
    random.shuffle(action_array)

    # If state action pair freq is under Ne, try it
    for action_choice in action_array:
        if freq_table[(d_state, action_choice)] < Ne:
            return action_choice

    # Else find the best next action
    for action_choice in range(-1, 2):
        if Q_table[(d_state, action_choice)] > max_q:
            max_q = Q_table[(d_state, action_choice)]
            best_action = action_choice

    return best_action


def next_max_Q(Q_table, disc_state, action, c_state):
    p = Pong(10,0)
    (next_state, next_c_state, reward) = p.predict_next_state(disc_state, action, c_state)
    max_next_Q = -math.inf

    if reward == -1:
        max_next_Q = -1

    for a in range(-1, 2):
        if (next_state, a) in Q_table and Q_table[(next_state, a)] > max_next_Q:
            max_next_Q = Q_table[(next_state, a)]
    return max_next_Q


def Q_learning(Q_table, freq_table, d_state, action, reward, c_state):
    if reward == -1:
        Q_table[(d_state, action)] = reward
        return 0
    else:
        freq_table[(d_state, action)] += 1
        old_Q = Q_table[(d_state, action)]

        alpha = C / (C + freq_table[(d_state, action)])
        Q_table[(d_state, action)] = old_Q + alpha * (reward + (gamma * next_max_Q(Q_table, d_state, action, c_state)) - old_Q)

        next_a = next_action(d_state, Q_table, freq_table)
        return next_a


