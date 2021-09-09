(ns newjackcity.nginx
  (:require [clojure.string :as string]))

(def parts1
  "Components of log lines to return"
  [:original :ip :remote-user :timestamp :request-method :request-uri :status-code :response-size :referrer])

(def parts2 
  "Components of log lines to return no request method"
  [:original :ip :timestamp :request-uri :status-code :response-size :referrer])

(def log-line-pattern1
 "Nginx log format"
   #"(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})? - (-|[^@]+@[^@]+\.[^@]+) \[(.*)\] \"(\w+) ([^\"]*)\" (\d{3}) (\d+) \"([^\"]*)\".*")

(def log-line-pattern2
 "Nginx log format where there is no request method"
 #"(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})? - - \[(.*)\] \"(.*)\" (\d{3}) (\d+) (\"-\").*")

(defn parse-line
  [line]
  (condp re-seq line
    log-line-pattern1 :>> (fn [[match]]
                            (apply hash-map (interleave parts1 match)))
    log-line-pattern2 :>> (fn [[match]]
                            (apply hash-map (interleave parts2 match)))))

(defn- logfile-lines
  [filename]
  (string/split-lines (slurp filename)))

(defn process-logfile 
  [filename]
    (map parse-line (logfile-lines filename)))

(defn nginx-logs 
  [filenames]
  (mapcat process-logfile filenames))

(nginx-logs ["blog.log"])
