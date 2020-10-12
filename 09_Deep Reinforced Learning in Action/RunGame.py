from keras.models import load_model

import numpy as np

import matplotlib.pyplot as plt
from matplotlib import animation

from IPython.display import display

from JSAnimation.IPython_display import display_animation

import gym                                

from time import sleep

env = gym.make('MountainCar-v0')  
model_name = "MountainCar_model_87"
model = load_model("trainedModels/" + str(model_name) + ".model")
def test():
    
    actions =  [0,0,0]
    state = env.reset()
    state_new = state
    moves = 0
    done = False
    frames = []
    
    while not done:
        moves += 1
        sleep(.05)

        Q = model.predict(state_new.reshape(1,2)) 
        action = np.argmax(Q[0])

        actions[action] += 1
        print(action)
        frames.append(env.render(mode = 'rgb_array'))
        state_new, reward, done, info = env.step(action)

        state = state_new   

        if done:
            print('Total reward: {}. Actions: {}'.format(moves, actions))
            save_as_animated_gif(frames)
            save_as_snapshot_gif(frames)
            return
    pass

def get_input_vector(state, new_state):
    input_vector = np.zeros(4)
    input_vector.put([0,1],state)
    input_vector.put([2,3],new_state)
    return input_vector.reshape(1,4)

def save_as_snapshot_gif(frames):
    plt.imshow(frames[-10])
    plt.axis('off')
    plt.savefig(str(model_name) + '_snapshot.png')

def save_as_animated_gif(frames):
    plt.figure(figsize=(frames[0].shape[1] / 72.0, frames[0].shape[0] / 72.0), dpi = 72)
    patch = plt.imshow(frames[0])
    plt.axis('off')

    def animate(i):
        if(i < len(frames)):
            patch.set_data(frames[i])
        else:
            patch.set_data(frames[-1])

    anim = animation.FuncAnimation(plt.gcf(), animate, frames = len(frames) + 10, interval=50)
    anim.save(str(model_name) + '.gif', fps=20)
    display(display_animation(anim, default_mode='loop'))

test()

