#!/c/msys64/mingw64/bin/python

import os, subprocess

# setting classpath for jdk
os.environ["CLASSPATH"] = ".;C:/Program Files/OpenJDK/javafx-sdk-17.0.1/lib/*;C:/Program Files/OpenJDK/gson-2.2.2.jar;C:/Program Files/OpenJDK/jcuda-10.1.0/*"

# cleaning bin/app directory
for file in os.listdir("./bin/app") :
    os.remove("./bin/app/" + file)

# compiling java files of app package to app directory in bin
print(subprocess.getoutput("javac -d ./bin ./src/app/*.java"))

# running the application
os.chdir("./bin")
print(subprocess.getoutput("java --module-path \"C:/Program Files/OpenJDK/javafx-sdk-17.0.1/lib\" --add-modules javafx.controls,javafx.fxml app.Container"))
