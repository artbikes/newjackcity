(ns newjackcity.core
  (:require [clj-time.core :as ttime]
           [clj-time.coerce :as coerce]
           [clj-time.format :as tformat]
           [incanter.core :as i]
           [incanter.charts :as c]
           [newjackcity.nginx :as ngx]))


(def timestamp-format "dd/MMM/yyy:HH:mm:ss Z")

(defn parse-datetime [timestamp]
  (tformat/parse (tformat/formatter timestamp-format) timestamp))

;; (parse-datetime "18/Mar/2013:15:18:49 -0500")

(defn timestamp-in-millis [record]
  (coerce/to-long (parse-datetime (:timestamp record))))


(def timestamp-resolutions
   {:millis 1
    :second 1000
    :minute (* 1000 60)
    :15-minutes (* 1000 60 15)
    :hour (* 1000 60 60)})

(defn timestamp-to-resolution [record resolution]
  (let [r (resolution timestamp-resolutions)]
    (* r (quot (timestamp-in-millis record) r))))

(defn timestamp-minute [record]
  (timestamp-to-resolution record :minute))


(defn count-entries [records & {:keys [by]}]
  (frequencies (map by records)))

(defn count-entries-dataset [records & {:keys [by grouping-name]}]
  (i/col-names (i/to-dataset (map identity (count-entries records :by by))) [grouping-name "Hits"]))

(defn -main
  "I don't do a whole lot ... yet."
  []
  (ngx/nginx-logs ["test.log"]))

(count-entries (ngx/nginx-logs ["nginx_sample.log"]) :by timestamp-minute)
;; (count-entries-dataset (ngx/nginx-logs ["test.log"]) :by timestamp-minute :grouping-name "Minute")

(i/view (c/time-series-plot 
    "Minute"
    "Hits"
    :data (count-entries-dataset (ngx/nginx-logs ["test.log"]) :by timestamp-minute :grouping-name "Minute")))

;; this is what dude said would work
;; (view-time-series (nginx-logs [path-to-nginx-log file]) :by timestamp-minute :grouping-name "minute")
