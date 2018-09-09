# ScrumTool
An android tool for the scrum. Developed by George Marmaris during the Thesis "Management, monitoring and control of IT projects - development of android software which supports the method scrum" of the department Engineering Project Management MSc of the Hellenic Open University.

This project works with the Firebase Console, where the data are saved. I have created a project on Firebase and its data are on file: app/google-services.json . 

If you want to use this app with your own firebase project (and database) then:

Create a new project on Firebase Console (https://console.firebase.google.com).
On Database -> Rules write:

{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}

On Authentication -> Sign-in method enable Email/Password and on Authentication -> Users add your uses with emal and password. These users will have acces to your app.

On your project settings you can download your google-services.json file and replace it on app folder. 
This way this app can be used with your own firebase database!

I ypu want to test this app with my project you can use the user:

email: scrumtooltester@gmail.com
password: tester

Warning: this app was developed on 2017 for android 5 as a part of my thesis. Since then it is not updated, so on future android versions you should have to make some changes (deprecations, bugs etc..).

