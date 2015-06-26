javaListsInMeteor
====================  

javaListsInMeteor contains 2 Maven projects:  
   1.  jddpclient - a basic Meteor DDP client;  
   2.  java-lists-in-meteor - contains classes that allow you to send, receive java.util.list items to/from the Meteor project SubscriptTables,  and susbscribe to changes that occur on SubscriptTables.  

WHAT IT DOES:  
The maven project java-lists-in-meteor shows the use of a class  
com.billybyte.meteorjava.MeteorListSendReceive.  

This class has all of the code to:  
   1.  Create a DDP connection with the Meteor project SubscriptTables (https://github.com/bgithub1/SubscriptTables).  
   2.	Login to the Meteor project SubscriptTables.  You must have that project running.  
   3.	Send java.util.list instances of the java class 
			com.billybyte.meteorjava.MeteorTableModel.  
	SubscriptTables stores these TableModels in a collection, and then uses them to display  
	other java.util.list data that is sent by MeteorListSendReceive.  
   4.	Send java.util.list instances of any class that has an _id field and a useId field.  
	You can extend the class com.billybyte.meteorjava.MeteorBaseListItem which has these 2 fields  
	or you can simply add those fields to your own classes.  I expected that users of this maven project,  
	as well as users of SubscriptTables, would send to SubscriptTables "wrapper" class instances (adpater pattern) of their
	existing classes to control which fields in their existing classes get shown in Meteor.
   5.  Receive java.util.list instances back from Meteor.
   6.  Subscribe to changes in those instances that have been sent to Meteor.

SETUP:  
To use java-lists-in-meteor:  
   1. Clone the github repo at https://github.com/bgithub1/javaListsInMeteor ;
   2. Clone the githib repo for SubscriptTables at https://github.com/bgithub1/SubscriptTables ;
   3. Deploy (locally or on meteor.com) the Meteor project SubscriptTables ;
   4. Within a bash terminal, run the sh script:   
      sh runSetupTableModelsAndSendReceiveLists.sh (it is located in the folder java-lists-in-meteor in the folder to which you cloned javaListsInMeteor)
      This script will use the url localhost and the port 3000 to communicate with Meteor.
      It will also login to meteor using the admin email admin1@demo.com and the admin password "admin1" (no quotes).
      If you want to change these, see the java class com.billybyte.meteorjava.runs.SetupTableModelsAndSendReceiveLists for details about specifing command line args like "metUrl=localhost" .
   5.  At this point, you should have several collections in your deployed Meteor project:  
      masterTableDefs - a collecton that holds the instances of com.billybyte.meteorjava.MeteorTableModel that you sent to Meteor in runSetupTableModelsAndSendReceiveLists.sh ;  
      *  misc.PosClDetailed - a collection of instances of misc.PosClDetailed (in JSON);  
      *  misc.Trades - another example of java instances sent to Meteor ;  
      *  misc.HowTos - another example of java instances sent to Meteor ;  




