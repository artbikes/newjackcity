(ns newjackcity.visualization
  (:require [incanter.core :as i]
            [incanter.charts :as c]))

(defn view-time-series 
  [& {:keys [grouping-name]}]
  (prn :keys))

(view-time-series :grouping-name "minute")

(defn map-set
  [records]
    (i/to-dataset [records]))


