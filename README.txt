PHONE WEBCAM - Android Camera -> PC Virtual Webcam
=====================================================

EPPADI VELA SEYYUM?
---------------------
1. Phone-la "PhoneWebcam" app open pannunga -> adhu camera-va capture
   pannitu, WiFi vazhiya ஒரு URL-la stream pannum:
   http://<phone-IP>:8080/video   (idha browser-layum direct open
   pannalam, moving image varum - test panna)

2. PC-la phone_webcam.py (allathu PhoneWebcam.exe) run pannunga ->
   adhu antha stream-a padichu, "Virtual Camera" nu ஒரு fake
   webcam device create pannidum.

3. Zoom / Google Meet / Teams / OBS - edhu venalum, Camera settings-la
   "OBS Virtual Camera" nu தேர்ந்தெடுத்தா, phone camera தான் webcam
   ஆக வேலை செய்யும்.

QUICK START (Source-a direct run panna):
------------------------------------------
PC:
    pip install opencv-python pyvirtualcam pyinstaller
    OBS Studio install pannunga (driver mattum thevai):
    https://obsproject.com/

    python pc/phone_webcam.py --phone-ip 192.168.1.50

Android:
    Android Studio-la "android" folder open pannunga, Run pannunga
    (allathu "Build APK(s)" pannunga)

.EXE / .APK VENUMA? (cloud build, PC-la onnum install pannama):
-------------------------------------------------------------------
Munnadi kudutha "HOW_TO_GET_EXE_AND_APK.txt" steps-ah follow pannunga
(idhe method intha project-kum vela seyyum):

1. GitHub-la free account + repo create pannunga
2. Intha zip-la irukkura files-a (pc, android, .github) அப்படியே
   upload pannunga
3. "Actions" tab-la automatic build start aagum (~5 nimisham)
4. "Artifacts" section-la PhoneWebcam-exe, PhoneWebcam-apk
   download pannikalam

NOTE:
- Phone-um PC-um same WiFi network-la irukkanum
- Phone app background-la run aagum pothu screen off aana stream
  nirkalam (phone settings-la "battery optimization" OFF pannunga
  intha app-ku, continuous run aaga)
- Low light-la quality konjam kammi irukkum (JPEG quality 70% vechi
  irukkom, pc/phone_webcam.py-la "quality" value maathalaam)
