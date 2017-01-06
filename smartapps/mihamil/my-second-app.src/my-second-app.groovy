/**
 *  My First App
 *
 *  Copyright 2017 Michael Hamilton
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "My Second App",
    namespace: "mihamil",
    author: "Michael Hamilton",
    description: "My Second SmartApp",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Turn on when motion detected:") {
		input "themotion", "capability.motionSensor", required: true, title: "Where?"
	}
    section("turn on this light"){
    	input "theswitch", "capability.switch", required: true
    }
    section("number of minues?"){
    	input "theminutes", "number", required:true, title: "Minutes?"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    log.debug "subscription created"
    subscribe(themotion, "motion.active", motionDetectedHandler)
    subscribe(themotion, "motion.inactive", motionStoppedHandler)
}

def motionDetectedHandler(evt) {
	
    log.debug "motionDetectedHandler called: $evt"
    theswitch.on()
}

def motionStoppedHandler(evt){
	log.debug "motionStoppedHandler called: $evt"
    runIn(60*theminutes, checkMotion)
}

def checkMotion(){
	log.debug "In checkMotion scheduled method"
    
    def motionState = themotion.currentState("motion")
    
    if (motionState.value == "inactive"){
    	def elapsed = now() - motionState.date.time
        log.debug("$elapsed ms elapsed since motion")
        def threshold = 1000*60*theminutes
        log.debug("$threshold threshold in ms")
        if (elapsed >= threshold){
        	log.debug "Motion has stayed inactive long enough since last check ($elapsed ms): turning switch off"
            theswitch.off()
        }else {
        	log.debug "Motion has not stayed inactive long enough since last check ($elapsed ms): doing nothing."
        }
    } else {
    	log.debug "Motion is active, do nothing and wait for inactive"
    }
}

// TODO: implement event handlers