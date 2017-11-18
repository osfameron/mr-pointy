(ns mr.pointy-test
  (:require [clojure.test :refer :all]
            [mr.pointy :refer :all]
            [clojure.data.xml :as xml]))

(deftest test-xml
  (xml/alias-uri :x "http://example.com")
  (let [x (xml (<x:episode id="2">
                 (<x:title> "This is a " (<b>"bold") (<i> "italic") " title")
                 (<x:ids>
                   (map #(xml (<x:id authority="me" {:type (:type %)}>
                                (:value %)))
                        [{:type "pid" :value "p0000001"}
                         {:type "uid" :value "abcdefgh"}]))))
        expected "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a:episode xmlns:a=\"http://example.com\" id=\"2\"><a:title>This is a <b>bold</b><i>italic</i> title</a:title><a:ids><a:id authority=\"me\" type=\"pid\">p0000001</a:id><a:id authority=\"me\" type=\"uid\">abcdefgh</a:id></a:ids></a:episode>"] 
    (is (= expected (xml/emit-str x)))))
