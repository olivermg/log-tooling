(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [log-reader.nodes-map :as nm]
            [log-reader.output :as o]))

(defn read-stream [in]
  (->> (repeatedly #(edn/read {:eof ::eof} in))
       (take-while #(not= % ::eof))
       (filter #(and (map? %)
                     (contains? % :trace)))))
