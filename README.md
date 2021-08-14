Android Road Quality Collector App
==================================

Road quality collection from cycling around. This only gathers and uploads data, for map generation / analysis go to https://github.com/RobertLucey/bike

Use a bike mount for the phone if you can, data from the phone being in the pocket isn't as good

Porting some of https://github.com/RobertLucey/bike to here because parts didn't arrive yet

## Features

- Cuts off the first and last x minutes (configurable) from your ride so nobody knows where you're coming and going to by sharing your ride info
	- Additionally it cuts first and last x metres. Whichever happens last, time or distance for the start or end takes effect
- Send journeys in partials which are segmented journeys where only data from a 200m span are grouped and no relative time is included
	- TODO - put a small gap between so these are more difficult to stitch together, stagger ability to download so can't stich together by time
- If time is included in uploaded data, it is only relative time from the start of the turning on of the service
