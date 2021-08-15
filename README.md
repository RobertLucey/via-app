Android Road Quality Collector App
==================================

Road quality collection from cycling around. This only gathers and uploads data, for map generation / analysis go to https://github.com/RobertLucey/bike

Use a bike mount for the phone if you can, data from the phone being in the pocket isn't as good

## Requirements

- Must not have any power saving enabled on your phone. If there is power saving it means the gps can't be updated when the phone is asleep
	- TODO find out if optimized and warn before starting services

## Features

- Cuts off the first and last x minutes (configurable) from your ride so nobody knows where you're coming and going to by sharing your ride info
	- Additionally it cuts first and last x metres. Whichever removes the most from either end takes effect
- Send journeys in partials which are segmented journeys where only data from a 200m span are grouped and no relative time is included
	- TODO - put a small gap between so these are more difficult to stitch together, stagger ability to download so can't stich together by time
- If time is included in uploaded data, it is only relative time from the start of the turning on of the service
