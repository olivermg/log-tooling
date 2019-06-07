(ns log-reader.printer.http
  (:require [clojure.java.io :as io]
            [hiccup.core :as h]
            [log-reader.printer :as p]
            [org.httpkit.server :as hs]))

(defn- load-public-resource [path]
  (-> (io/resource (str "public" path))
      slurp))

;;; NOTE: need to load these aot for being able to run as native-image:
(defonce app-js (load-public-resource "/js/app.js"))
(defonce app-css (load-public-resource "/css/app.css"))


(defn- build-document [content]
  (->> [:html
        [:head
         [:title "log-reader"]
         [:link {:rel "stylesheet" :href "app.css"}]]
        [:body
         content
         [:script {:src "https://code.jquery.com/jquery-3.4.1.min.js"
                   :integrity "sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
                   :crossorigin "anonymous"}]
         [:script {:src "app.js"}]]]
       h/html
       (str "<!DOCTYPE HTML>")))

(defn- make-handler [content]
  (let [document-response {:status  200
                           :headers {"Content-type" "text/html"}
                           :body    (build-document content)}
        app-js-response   {:status  200
                           :headers {"Content-type" "text/javascript"}
                           :body    app-js}
        app-css-response  {:status  200
                           :headers {"Content-type" "text/css"}
                           :body    app-css}]
    (fn [{:keys [uri] :as request}]
      (case uri
        "/"        document-response
        "/app.js"  app-js-response
        "/app.css" app-css-response
        {:status 404}))))

(defrecord HttpPrinter [port]

  p/Printer

  (print-line-xf [this]
    (fn [rf]
      (let [content (volatile! [:div])]
        (fn
          ([] (rf))
          ([result]
           (println (str "Serving HTTP on port ") port ", press Ctrl-C to quit.")
           (hs/run-server (make-handler @content)
                          {:port port})
           (rf result))
          ([result input]
           (vswap! content conj input)
           (rf result input)))))))

(defn construct [port]
  (map->HttpPrinter {:port port}))
