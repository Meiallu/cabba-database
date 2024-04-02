# CabbaDB

CabbaDB is a Java key-value database designed for simplicity and ease of use, serving both in-memory and disk-based storage requirements inspired by Redis.

# Setup

Setting up our software is a breeze. Just head over to the 'releases' section on GitHub, grab our .jar installer, and run it on your Linux machine. It will take care of all the configuration automatically. If you're using a different operating system, rest assured—the configuration files will be conveniently placed in the same directory where you ran the installer.

# Using

To effectively import the code for interacting and connecting with our database, you will need to incorporate our 'client' .jar file into your IDE. You can find this file in the releases section on GitHub. you can initiate the database with the following code:

```java
Cabba.connect("127.0.0.1", 6249, "password");
Cabba.createDiskDatabase("name");
```
To define a basic value, you can use the following code:
```java
Database diskDatabase = Cabba.getDatabase("name");
diskDatabase.set("key", "value");
```

That's the easiest it can get – with just four lines, you can set up and utilize a new disk database effortlessly.
