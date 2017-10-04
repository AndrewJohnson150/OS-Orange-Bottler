# Orange Bottler Lab
The goal of this lab was to learn the difficulties and joys of multi-threading with data and task parallelization.


## How I Met Requirements
This lab had several requirements: it requires task parallelization and data parallelization multi-threading. 
It also must be pushed to GitHub. I have completed all 3 of these tasks.
I have created multi-threading with plants, which is data parallelization.
This step specifically was partially implemented by Nate Williams before we even began working on the lab.
The next step, task parallelization, is accomplished by having 5 workers per plant.
Each worker gets work, does work, and gives work to the next worker thread. 
Finally, it is on GitHub (here). 
In addition, I have documented my code internally through Javadocs and externally through the UML diagram found in the Github repo, titled
UML_CLASS_DIAGRAM.png.


## Challenges
The challenges I faced included figuring out how to implement a queue that was synchronized and blocked so
that race conditions would be eliminated. 
Then, I had issues trying to successfully implement the task parallelization.
One of the biggest issues was stopping the plant at the end, and how to create the first worker such
that he would make the orange and not the Plant class.

