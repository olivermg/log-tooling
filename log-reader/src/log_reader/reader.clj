(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [log-reader.nodes-map :as nm]))

(defn- read-sexp [in]
  (try
    (edn/read {:eof ::eof} in)
    (catch Throwable e)))

(defn read-stream [in]
  (->> (repeatedly #(read-sexp in))
       (take-while #(not= % ::eof))
       #_(filter #(and (map? %)
                     (contains? % :trace)))))
