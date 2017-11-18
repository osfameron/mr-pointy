(ns mr.pointy
  (:require [clojure.data.xml :as xml])
  (:gen-class))

(declare xml-fn)

(defn resolve-namespace [ns n]
  (some-> n
          symbol
          ((ns-aliases ns) n)
          str))

(defn resolve-keyword [ns n t]
  (keyword (resolve-namespace ns n) t))

(defn make-resolve-keyword [[n t]]
 `(resolve-keyword *ns* ~n ~t))

(defn make-tag [[n t ended?]]
  [(make-resolve-keyword [n t]) ended?])

(defn guard-symbol? [a]
  (when (symbol? a) a))

(defn parse-tag [arg]
  (some->> arg guard-symbol?
           name
           (re-matches #"<(?:(\w+):)?(\w+)(>)?")
           rest
           make-tag))

(defn parse-attr [arg]
  (some->> arg guard-symbol?
           name
           (re-matches #"(?:(\w+):)?(\w+)=") 
           rest
           make-resolve-keyword))

(defn parse-attrs [[a & [b & cs :as bs]]]
  (when a
    (if-let [attr (parse-attr a)]
      (merge {attr b} (parse-attrs cs))
      (merge a (parse-attrs bs)))))

(defn split-exclusive [p coll]
  (let [[a [_ & b]] (split-with p coll)]
    [a b]))

(defn parse-attrs-and-contents [[kw ended?] tokens]
  (let [[attrs contents] (if ended?
                           [[] tokens]
                           (split-exclusive #(not= '> %) tokens))]
    [kw (parse-attrs attrs) contents]))

(defn tag [[a & bs :as form]]
  (if-let [tag (parse-tag a)]
    (let [[kw attrs contents] (parse-attrs-and-contents tag bs)]
      `(xml/element ~kw
                    ~attrs
                    ~@(mapv xml-fn contents)))
    form))

(defn xml-fn [form] 
  (if (seq? form)
    (tag form)
    form))

(defmacro xml [form] (xml-fn form))

(xml/alias-uri :foo "http://foo.com")
(xml/emit-str (xml (<foo:episode id="2">
                     (<foo:title> "This is a " (<b>"bold") (<i> "italic") " title")
                     (<foo:ids>
                       (map #(xml (<foo:id authority="me"
                                       {:type (:type %)}>
                                    (:value %)))
                            [{:type "pid" :value "p0000001"}
                             {:type "oid" :value "wibble"}])))))
