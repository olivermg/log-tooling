(defproject log-reader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[http-kit "2.3.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.9.0"]  ;; as 1.10 does not work currently with native-image + clojure.pprint/zprint.core
                 [tick "0.4.14-alpha"]
                 [zprint "0.4.15"]

                 ;;; to avoid version conflicts:
                 [org.clojure/tools.reader "1.2.2" :exclusions [org.clojure/clojure]]]
  :plugins [[io.taylorwood/lein-native-image "0.3.0"]]
  :main ^:skip-aot log-reader.core
  :target-path "target/%s"
  :native-image {;;;graal-bin ""  ;; set this via env var GRAALVM_HOME if not in PATH already
                 :name     "logreader"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts     ["--enable-http"
                            "--initialize-at-build-time"
                            "--no-fallback"
                            "--no-server"
                            #_"--static"  ;; as this doesn't seem to work with clojure 1.9
                            "--verbose"
                            "--report-unsupported-elements-at-runtime"
                            #_"-H:ConfigurationFileDirectories=resources/META-INF/native-image"
                            "-H:+ReportExceptionStackTraces"
                            #_"-H:+AllowVMInspection"
                            #_"--allow-incomplete-classpath"
                            #_"--initialize-at-run-time=clojure.spec.gen,clojure.spec.gen.alpha$dynaload"
                            #_"--initialize-at-build-time=clojure.spec.gen.alpha$fn__2632,clojure.spec.gen.alpha$fn__2835$bytes__2838,clojure.spec.gen.alpha$fn__2835$fn__2836,clojure.spec.gen.alpha$fn__2976,clojure.spec.gen.alpha$lazy_combinator,clojure.spec.gen.alpha$fn__2976,clojure.spec.gen.alpha$fn__2635,clojure.spec.gen.alpha$fn__2919$simple_type_printable__2922"
                            #_"--delay-class-initialization-to-runtime=clojure.spec.gen"]}
  :profiles {:uberjar {:aot :all}}
  :pedantic? :abort)
