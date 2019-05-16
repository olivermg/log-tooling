(ns log-reader.storage.nested-memory
  (:require [log-reader.nested-storage :as st]))

(defonce ^:private +nodes-map+ (atom {}))

(defn store-logline [nodes-map {:keys [trace] :as logline}]
  (let [[path nodes-map] (reduce (fn [[path nodes-map] {:keys [id] :as trace-step}]
                                   (let [path (conj path id)]
                                     [(conj path :children)
                                      (update-in nodes-map path #(assoc % :callinfo (dissoc trace-step :id)))]))
                                 [[] nodes-map]
                                 trace)
        #_path      #_(->> (map :id trace)
                       (interleave (repeat :children))
                       rest
                       vec)]
    (assoc-in nodes-map (conj path :message) (dissoc logline :trace))))

#_(defn lookup [nodes-map trace-ids]
  (let [path (-> (interleave (repeat :children) trace-ids)
                 vec)]
    (some-> (get-in nodes-map path)
            (update :children #(map (fn [[k v]]
                                      (conj (vec trace-ids) k))
                                    %)))))

(defn sorted-data [nodes-map]
  (->> (vals nodes-map)
       (sort #(compare (some-> %1 :callinfo :time)
                       (some-> %2 :callinfo :time)))))

(defn traverse [f {:keys [children] :as node}]
  (-> (update node :children
              (fn [children]
                (mapv (fn [[id child]]
                        (traverse f child))
                      children)))
      f))

(defn traverse-all [f nodes]
  (map (partial traverse f) nodes))



#_(let [nodes-map (-> {}
                    (store-logline {:trace [{:id :a :fn :afn :time 2} {:id :aa :fn :aafn :time 3}] :x "dataaa"})
                    (store-logline {:trace [{:id :b :fn :bfn :time 1}] :x "datab"})
                    (store-logline {:trace [{:id :a :fn :afn :time 3} {:id :ab :fn :abfn :time 4}] :x "dataab"})
                    (store-logline {:trace [{:id :a :fn :afn :time 5} {:id :ab :fn :abfn :time 6} {:id :aba :fn :abafn :time 7}] :x "dataaba"})
                    (store-logline {:trace [{:id :a :fn :afn :time 4} {:id :ab :fn :abfn :time 5} {:id :abb :fn :abbfn :time 6}] :x "dataabb"}))]
  (clojure.pprint/pprint nodes-map)
  (println "=====")
  (clojure.pprint/pprint (sorted-data nodes-map))
  (println "=====")
  (->> (traverse-all (fn [{:keys [callinfo message children] :as node}]
                       [:div.row
                        (let [{:keys [fn time]} callinfo]
                          [:div.callinfo
                           [:div.fn fn]
                           [:div.time time]])
                        [:div.message
                         [:pre message]]
                        (into [:div.children] children)])
                     (sorted-data nodes-map))
       (into [:div])
       zprint.core/zprint))
