(ns log-reader.formatter.text
  (:require [clojure.edn :as edn]
            [clojure.string :as s]
            [log-reader.formatter :as f]))

(defn- format-trace [{:keys [args fn id time] :as trace}]
  (mod id 10000))

(defn- format-traces [traces]
  (->> (map format-trace traces)
       (s/join " ")))

(defrecord TextFormatter []

  f/Formatter

  (format-line [this {:keys [data msg name ns time trace] :as line}]
    (->> [(str "[" time "] (" (format-traces trace) ") " name) msg data]
         (remove nil?)
         (s/join ", "))))

(defn construct []
  (map->TextFormatter {}))
