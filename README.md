# ToDoList
A simple ToDoList application that makes use of an SQLlite database in Android, along with new features of the support library.

Build instructions:</br>
1. Compile gradle project with minimum SDK version 14 and maximum SDK version 22 and build tools 22.0.1 </br>
2. Run the project.

This project simply allows you to create a ToDo list and manage that todo list through updating and deleting. When your ToDo entry 
is close to its deadline, the entry will highlight to notify you that you are past your deadline. The project also makes use
of an SQLlite database. 

Assumptions
Some assumptions that I had with this project was that the toDo's only needed to be created and viewed and nothing else. As mentioned
All CRUD operations are available such as marking a todo entry as complete but as far as editing the todo I did not add (It's so
simple to create a todo, why not make a new one?). Also the design follows the Material Design guidelines. I did not make use of
any api or interface because I did not see how this could have been implemented into the Android Application as per the requirements
for each. I could have easily made the application and endpoint for a webservice but then what? All the data is stored locally. 
For examples of how I consume restful webservices, please view my other repositories here on Github.

Optimizations
I can optimize this code by allowing the ToDo entry to update automatically whenever the instance is created and the set() methods
are accessed. This will eliminate the code where we update the front end then update the back end and make the application a little bit
more autonomous.
