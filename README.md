# SOSHelper_Final
this is a android mobile application I did during the internship.

•	Collaborated with team of software engineer developer and built an Android application on Eclipse based on an instant communication platform.
•	Worked on help caller feature which can receive an send different kinds of messages automatically.
•	Contributed to UI design and software troubleshooting to improve user experience & tested capabilities of products.

Motivation:

SOS helper is an android mobile app, our motivation is that when people face some danger, it may be difficult to call 911 in some special situation. In our project, we created an android app to help people send helping message with the simplest operation and can keep communication with other automatically, continuously and imperceptibly.

because our product need to communicate with others, so that means we need a server to support our idea.
our project is based on Easemob which offers us advanced instant communication cloud platform and open source sdk. With this sdk, we can implement the most basic communication fearures such as chatting with single person and chatting in a chatting group.
Based on Easemob, we modify and add our own features and primarily use service to send broadcast and then we use broadcast receiver to call easemob sdk to implement our functionality. Finally, our application can be started with a button on lock screen, and send helping message, real time voice recording and location information to emergency group.
：
our project is divided into 2 parts, front end and back end, I’m in charge with back end, and I primarily focus on create a service that can send message, voice, and address information to emergency group periodically, and my friend made a lock screen which can make users launch our application as fast as possible and a emergency group setting activity that users can make a basic settings.

1.	change lock screen and add our app in lock screen that user can start our app directly. Before, in order to start the service of our application, it will take 4 steps: turn on the screen, unlock the screen, open SOS helper, and press the start button. Because android doesn’t provide any API for modifying the system lock-screen. So instead of modify it, we decide to create a new lock screen. If users click on our app icon, it will activate the SOS service in silence.
2.	In the original Easemob, users can just communicate with each other by text message, In our project, we add some features such that users can send voice message and their location information. And we created a service to collect those information and then send them to server. (the reason we create a service because it can run quietly in background and service can keep running until the users stop it or until the mobile is out of power, and it wouldn’t be interrupted by other application). The service will send 3 broadCast, and we created a SOSReceiver class to extends BroadcastReceiver that can receive those 3 broadcast, once it receive the broadcast, it will call sendMessage sdk which Easemob offers to us to send message to emergency group through server.
3.	About the google map, our project offer two ways to get the accurate location information, GPS and WIFI, the mobile can choose the better one according to users environment. Once the locationManager get the latitude and longitude, we use asynckTask to transfer it to 
4.	the information of the group name, contact list and setting preference was stored in SQLite （sqlite is embedded into every Android device, it’s an Open Source databse, support standear relational database features like SQL）.

in the first place, I found that the google_map activity is not stable, I thought something wrong with google_map API installation process, and I tryied over and over again and talked to TA for many times, finally I found that this is because when we transfer longitude and latitude to address information, sometimes it will block the UI thread, so I use AsyncTask to do this transfer job. (AsyncTask allows to perform background operation and publish result on the UI thread).


improvement：
if our mobile application can be improved, we would like to make it more intelligent by setting frequence and voice recording time according to the power consumption. That means, if there is little power left, our application can adjust it’s frequence like every minutes instead of every 15 seconds in order to working longer time.

