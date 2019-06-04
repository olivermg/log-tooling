(ns log-reader.processor
  (:require [clojure.edn :as edn]))

(defn- parse-edn [edn]
  (cond
    (map? edn)    (->> edn
                       (map (fn [[k v]]
                              [k (parse-edn v)]))
                       (into {}))
    (string? edn) (try
                    (edn/read-string edn)
                    (catch Exception e
                      edn))
    true          edn))

(defn- is-logexpr? [expr]
  (and (map? expr)
       (contains? expr :trace)
       (contains? expr :name)))

(defn- refine [expr]
  (update expr :data parse-edn))

(defn processor-xf []
  (comp (filter is-logexpr?)
        (map refine)))