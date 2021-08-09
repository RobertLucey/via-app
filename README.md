Android Road Quality Collector App
==================================

Road quality collection from cycling around. This only gathers and uploads data, for map generation / analysis go to https://github.com/RobertLucey/bike

Use a bike mount for the phone if you can, data from the phone being in the pocket isn't as good

Porting some of https://github.com/RobertLucey/bike to here because parts didn't arrive yet

## Features

- Cuts off the first and last x minutes (configurable) from your ride so nobody knows where you're coming and going to by sharing your ride info
	- Additionally it cuts first and last x metres. Whichever happens last, time or distance for the start or end takes effect
- (TODO - look at partials in https://github.com/RobertLucey/bike for reference) Optional mix multiple journeys together and split them again randomly by coordinates so it is unlikely any route will be found (along with a few other privacy measures)
- If time is included in uploaded data, it is only relative time from the start of the turning on of the device
