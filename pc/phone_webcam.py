"""
Phone Webcam - PC side
=======================
Android phone camera stream-a padichu, adha PC-la ஒரு "Virtual Camera"
ஆக கொடுக்கும் — Zoom, Google Meet, Teams, OBS etc எல்லாத்துலயும்
intha virtual camera-va webcam madhiri select pannikalam.

Run pannurathukku munnadi:
    pip install opencv-python pyvirtualcam

pyvirtualcam Windows-la vela seyya:
    OBS Studio install pannunga (https://obsproject.com/) - adhuloda
    "OBS Virtual Camera" driver automatic install aagidum.
    (OBS app-a open panna venam, driver mattum thevai)

Run:
    python phone_webcam.py --phone-ip 192.168.1.50

Build to .exe:
    pip install pyinstaller
    pyinstaller --onefile --name PhoneWebcam phone_webcam.py
"""

import argparse
import sys

try:
    import cv2
except ImportError:
    print("opencv-python install pannunga: pip install opencv-python")
    sys.exit(1)

try:
    import pyvirtualcam
except ImportError:
    print("pyvirtualcam install pannunga: pip install pyvirtualcam")
    sys.exit(1)


def main():
    parser = argparse.ArgumentParser(description="Phone camera -> PC Virtual Webcam")
    parser.add_argument("--phone-ip", required=True, help="Android phone-oda WiFi IP")
    parser.add_argument("--port", type=int, default=8080)
    parser.add_argument("--preview", action="store_true", help="Oru window-la preview-um kaamikkanuma")
    args = parser.parse_args()

    url = f"http://{args.phone_ip}:{args.port}/video"
    print(f"Connecting to phone stream: {url}")

    cap = cv2.VideoCapture(url)
    if not cap.isOpened():
        print("Phone stream open aagala. Check panunga:")
        print(" - Phone app run aaguthaa, same WiFi-la irukka")
        print(" - IP address correct-aa")
        sys.exit(1)

    ok, frame = cap.read()
    if not ok:
        print("Frame padikka mudiyala. Phone app-la camera start aachaa nu check pannunga.")
        sys.exit(1)

    height, width = frame.shape[:2]
    print(f"Stream resolution: {width}x{height}")

    with pyvirtualcam.Camera(width=width, height=height, fps=20) as cam:
        print(f"Virtual camera started: {cam.device}")
        print("Ipo Zoom/Meet/Teams-la 'Camera' dropdown-la intha virtual camera-va select pannunga.")
        print("Stop panna Ctrl+C")
        try:
            while True:
                ok, frame = cap.read()
                if not ok:
                    continue
                frame = cv2.resize(frame, (width, height))
                frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                cam.send(frame_rgb)
                cam.sleep_until_next_frame()

                if args.preview:
                    cv2.imshow("Phone Webcam Preview", frame)
                    if cv2.waitKey(1) & 0xFF == ord('q'):
                        break
        except KeyboardInterrupt:
            pass
        finally:
            cap.release()
            cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
