(ns newjackcity.core-test
  (:require [clj-time.core :as time]
            [clj-time.coerce :as coerce])
  (:use midje.sweet
        newjackcity.core))

(def timestamp "21/Apr/2013:15:47:10 -0500")
(def timestamp-same-hour "21/Apr/2013:15:59:59 -0500")
(def timestamp-as-datetime (time/date-time 2013 04 21 20 47 10))

(fact "Can parse a date string into a DateTime"
  (parse-datetime timestamp) => timestamp-as-datetime)

;; (fact "Can get a log entry with time in miliseconds"
;;   (with-timestamp-in-millis {:timestamp timestamp}) => {:timestamp timestamp
;;                                                         :timestamp-in-millis (coerce/to-long timestamp-as-datetime)})

(facts "Can get the timestamp to a resolution"
  (fact "Can get the timestamp to the minute"
    (timestamp-minute {:timestamp timestamp}) => (coerce/to-long (time/date-time 2013 04 21 20 47))))
