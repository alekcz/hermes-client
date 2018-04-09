(set-env! :dependencies '[[org.clojure/clojure "1.8.0"]
                          [clj-http "3.8.0"]])

(require '[boot.core :as boot])
(require '[clojure.java.io :as io])
(require '[clj-http.client :as client])
(require '[clojure.java.shell :as shell])

(deftask deploy
	"Sends deployment payload to hermes server" 
  	[ s server SERVER str "target deployment server"
	    k key KEY str "authentication key"
      p payload PAYLOAD str "the deployment payload"]
    (if server 
      (let [_payload (if payload payload "dist/") tmp (boot/tmp-dir!)]
        (let [target "payload.tar.gz" ]
          (println target)
          (shell/sh "tar" "-zcvf" target _payload)
          (client/post server
            {:multipart [{:name "payload" :content (clojure.java.io/file target)}]})))

      (println "Invalid parameters run: hermes deploy -h for help")))
      

(deftask login
	"Authenticates the current user" 
  	[ s server SERVER str "target deployment server"]
	(println "Coming soon"))