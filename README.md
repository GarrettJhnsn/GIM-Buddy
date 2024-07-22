
# GIM Buddy
*0.1.0-alpha.1*

**GIM Buddy** is currently in alpha development stage. As such, you may encounter bugs and incomplete features. Your feedback is invaluable to us as we continue to improve and enhance the plugin.

How to Report Bugs
If you encounter any issues or have suggestions for improvements, please report them by opening an issue on our GitHub repository. We appreciate your patience and support as we work towards delivering a stable and feature-complete plugin.

We are committed to making constant improvements to GIM Buddy. Stay tuned for regular updates that will bring new features, optimizations, and bug fixes.

## Key Features

### **Bank Sharing** ✅
* **Multi-Bank Views:** Effortlessly view your personal bank, individual group member banks, and the shared group storage.
* **Advanced Filtering/Sorting:** Quickly locate specific items using robust filtering and sorting options.
* **Firestore Integration:** Real-time bank updates across all group members, powered by a secure Firestore database connection.

### **Firebase Integration** ✅
* **Secure Authentication:** Personalized Firebase authentication for enhanced security and privacy.
* **Centralized Data:** Utilize Firestore for reliable and efficient storage of all group data.

### **Node.js Server** ✅
* **Docker Integration:** The server comes with a Dockerfile ready to be built, making it easy to deploy the server to any cloud resources. Using Docker ensures that the server environment is consistent across different deployment platforms and always online.
* **Portable:** The server can be quickly set up on any system that supports Node.js. This portability ensures that you can run the server on your local machine or on any production server without needing extensive configuration.

### **Item Tracking** ❌
* **Targeted Tracking:** Select specific items for the group to track collectively.
* **Instant Notifications:** Receive immediate notifications when a tracked item is acquired by any group member, including details on who found it and when.


## Node.js Server Setup

**Note:** This plugin requires the setup of a Firestore Database and Node.js server at the moment.

To enable full functionality of the GIM Buddy plugin, you need to set up the GIM Buddy Server. Follow the instructions in the [GIM Buddy Server README](https://github.com/fNudz/gim-buddy-server/blob/main/README.md).


## 🚧  Development Progress  🚧

| Feature                             | Status         |
|-------------------------------------|----------------|
| Bank Sharing                        | ✅ Completed   |
| Firestore Authentication & Database | ✅ Completed  |
| Node.js Server                      | ✅ Completed  |
| Item Tracking                       | ❌ Not Started |


## Known Issues and Limitations

- Item icons not properly loading at first in bank view on first load.

If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request.
