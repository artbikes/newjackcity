(ns newjackcity.nginx-test
  (:use midje.sweet
        newjackcity.nginx))

(def logline "74.125.225.243 - - [18/Mar/2013:15:18:49 -0500] \"GET /index?key=val\" 302 197 \"http://www.amazon.com/gp/prime?ie=UTF8&*Version*=1&*entries*=0\" \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)\" \"-\" \"got: uid=C8D925AED17547510D26082802B0C116\" \"set: -\" 1363637929.657")
(def logline2 "173.252.110.27 - - [18/Mar/2013:15:20:10 -0500] \"PUT /logon\" 404 1178 \"http://shop.github.com/products/octopint-set-of-2\" \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)\" \"-\" \"got: uid=C3D0C0AD617547517407B52502BD72AD\" \"set: -\" 1363638010.685")
(def logline-no-ipaddress " - - [18/Mar/2013:15:20:59 -0500] \"POST /search?id=1234567890\" 200 2 \"http://www.google.com\" \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)\" \"-\" \"got: uid=C3D0C0AD617547517407B52502BD72AD\" \"set: -\" 1363638010.685")
(def logline-remote-user "185.30.177.37 - zaab@chinet.com [02/Sep/2021:15:19:12 +0000] \"POST /autodiscover/autodiscover.xml HTTP/1.1\" 405 711 \"https://45.79.25.48/autodiscover/autodiscover.xml\" \"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Win64; x64; Trident/6.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3; Tablet PC 2.0; Microsoft Outlook 15.0.4481; ms-office; MSOffice 15)\" \"-\"
")
(def logline-poodle "144.86.173.65 - - [01/Sep/2021:01:18:28 +0000] \"\\x16\\x03\\x01\\x00\\xE1\\x01\\x00\\x00\\xDD\\x03\\x03\\xEDJi\\xA8Z~\\x19\\xF3\\xE2+\\x9C\\x04F1X\\x98o\\x1EP\\xC6\\xE9I\\xEA\\x97B\\x14\\xEE,w\\xF9\\x88\\xAB\\x00\\x00h\\xCC\\x14\\xCC\\x13\\xC0/\\xC0+\\xC00\\xC0,\\xC0\\x11\\xC0\\x07\\xC0'\\xC0#\\xC0\\x13\\xC0\\x09\\xC0(\\xC0$\\xC0\\x14\\xC0\" 400 354 \"-\" \"-\" \"-\"")


(def logline-seq [logline logline2 logline-no-ipaddress])

(facts "We can parse Nginx log lines into its components"
  (fact "We can get the original log line from the parsed log line"
    (:original (parse-line logline)) => logline
    (:original (parse-line logline2)) => logline2
    (:original (parse-line logline-no-ipaddress)) => logline-no-ipaddress)
  (fact "We can get the IP Address from a log line"
    (:ip (parse-line logline)) => "74.125.225.243"
    (:ip (parse-line logline2)) => "173.252.110.27"
    (:ip (parse-line logline-no-ipaddress)) => nil)
  (fact "We can get the Timestamp from a log line"
    (:timestamp (parse-line logline)) => "18/Mar/2013:15:18:49 -0500"
    (:timestamp (parse-line logline2)) => "18/Mar/2013:15:20:10 -0500"
    (:timestamp (parse-line logline-no-ipaddress)) => "18/Mar/2013:15:20:59 -0500")
  (fact "We can get the request method from a log line"
    (:request-method (parse-line logline)) => "GET"
    (:request-method (parse-line logline2)) => "PUT"
    (:request-method (parse-line logline-no-ipaddress)) => "POST")
  (fact "We can get the request uri from a log line"
    (:request-uri (parse-line logline)) => "/index?key=val"
    (:request-uri (parse-line logline2)) => "/logon"
    (:request-uri (parse-line logline-no-ipaddress)) => "/search?id=1234567890")
  (fact "We can get the status code from a log line"
    (:status-code (parse-line logline)) => "302"
    (:status-code (parse-line logline2)) => "404"
    (:status-code (parse-line logline-no-ipaddress)) => "200")
  (fact "We can get the repsonse size from a logline"
    (:response-size (parse-line logline)) => "197"
    (:response-size (parse-line logline2)) => "1178"
    (:response-size (parse-line logline-no-ipaddress)) => "2")
  (fact "We can get the referred from a logline"
    (:referrer (parse-line logline)) => "http://www.amazon.com/gp/prime?ie=UTF8&*Version*=1&*entries*=0"
    (:referrer (parse-line logline2)) => "http://shop.github.com/products/octopint-set-of-2"
    (:referrer (parse-line logline-no-ipaddress)) => "http://www.google.com"))

;; maybe put parse-lines back in at some point?
;; (fact "We can parse multiple lines"
;;   (count (parse-lines logline-seq)) => (count logline-seq))

(fact "We can parse loglines from file(s)"
  (fact "We can parse loglines from a single file"
    (count (process-logfile "test/newjackcity/nginx_sample.log")) => 4)
  (fact "We can parse loglines from multiple files"
    (count (nginx-logs ["test/newjackcity/nginx_sample.log" "test/newjackcity/nginx_sample.log"])) => 8))
