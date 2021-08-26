Android Road Quality Collector App
==================================

<img src="/assets/logo.png" alt="via logo" style="height: 100px; width:100px;"/>

Road quality collection from cycling around. This only gathers and uploads data, for map generation / analysis go to https://github.com/RobertLucey/via

Use a mount for the phone if you can for bikes.

## Requirements

- Must not have any power saving enabled on your phone. If there is power saving it means the gps can't be updated when the phone is asleep
	- TODO find out if optimized and warn before starting services

## Features

- Cuts off the first and last x minutes (configurable) from your ride so nobody knows where you're coming and going to by sharing your ride info
	- Additionally it cuts first and last x metres. Whichever removes the most from either end takes effect
- Send journeys in partials which are segmented journeys where only data from a 200m span are grouped and no relative time is included
	- Put a small gap between so these are more difficult to stitch together
	- TODO: stagger ability to download so can't stich together by data availability time
- If time is included in uploaded data, it is only relative time from the start of the turning on of the service
	- TODO: If not sending relative time the journey is randomly reversed (or each partial is randomly reversed) so direction can not be determined
