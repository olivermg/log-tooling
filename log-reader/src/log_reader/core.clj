(ns log-reader.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [log-reader.reader :as r]
            [log-reader.nodes-map :as nm]
            [log-reader.formatter :as f]
            [log-reader.formatter.html :as fh]
            [log-reader.formatter.text :as ft]
            [log-reader.printer :as p]
            [log-reader.printer.http :as ph]
            [log-reader.printer.stdout :as ps]
            [log-reader.webserver :as w]))

(defn- getopts [args]
  (->> args
       (partition 2)
       (map (fn [[[_ & ks] v]]
              [(->> ks (apply str) keyword) v]))
       (into {})))

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

(defn -main [& args]
  (let [{:keys [format] :as opts} (getopts args)
        [formatter printer] (case (keyword format)
                              :html [(fh/construct) (ph/construct 8888)]
                              [(ft/construct) (ps/construct)])
        xf        (comp (filter #(and (map? %) (contains? % :trace) (contains? % :name)))
                        (map #(update % :data parse-edn))
                        (f/format-lines-xf formatter))
        input     (r/read-stream *in*)
        output    (into [] xf input)]
    (p/print-lines printer output)))



#_(let [#_in-str #_(str "{:trace [{:id :a :fn :afn :args :aargs :time 2} {:id :aa :fn :aafn :args :aaargs :time 3}] :x :aa}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 1} {:id :ba :fn :bafn :args :baargs :time 2}] :x :ba}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 3} {:id :bb :fn :bbfn :args :bbargs :time 4} {:id bba :fn :bbafn :args :bbaargs :time 5}] :x :bba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5}] :x :ab}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :aba :fn :abafn :args :abaargs :time 6}] :x :aba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :abb :fn :abbfn :args :abbargs :time 7}] :x :abb}")
      in-str (slurp "testlog.log")]
  (with-in-str in-str
    (let [srv (-main)]
      (Thread/sleep 10000)
      (srv))))
