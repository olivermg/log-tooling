(ns log-reader.formatter.text
  (:require [clojure.edn :as edn]
            [clojure.string :as s]
            [log-reader.formatter :as f]
            [tick.alpha.api :as t]
            [zprint.core :as zp]))

(defn- format-trace [{:keys [args fn id time] :as trace}]
  (mod id 10000))

(defn- format-traces [traces]
  (->> (map format-trace traces)
       (s/join " ")))

(defrecord TextFormatter []

  f/Formatter

  (format-line [this {:keys [column data file fn level line msg name ns time trace] :as line}]
    (let [utctime (or (and time (t/format :iso-instant
                                          (t/instant time)))
                      "???")
          level   (or (some-> level clojure.core/name s/upper-case) "???")
          name    (or name "???")
          file    (or file "???")
          fn      (or fn "???")
          line    (or line -1)
          base    (format "[%s] %-5s (%s) <%s:%s:%d> %s"
                          utctime level (format-traces trace) file fn line name)]
      (->> [base msg data]
           (remove nil?)
           (s/join ", ")))))

(defn construct []
  (map->TextFormatter {}))
