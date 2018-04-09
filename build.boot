(set-env! :dependencies '[[org.clojure/clojure "1.8.0"]
                          [clj-http "3.8.0"]
                          [danlentz/clj-uuid "0.1.7"]
                          [me.raynes/fs "1.4.6"]
                          [org.clojars.pmonks/spinner "0.4.0"]])

(require '[boot.core :as boot])
(require '[clojure.java.io :as io])
(require '[clj-http.client :as client])
(require '[clojure.java.shell :as shell])
(require '[clj-uuid :as uuid])
(require '[me.raynes.fs :as fs])
(require '[spinner.core :as spin] :reload-all)


(deftask deploy
	"Sends deployment payload to hermes server" 
  	[ s server SERVER str "target deployment server"
	    k key KEY str "authentication key"
      p payload PAYLOAD str "the deployment payload"]
    (if server 
      (let [_payload (if payload payload "dist/") tmp (boot/tmp-dir!)
            payloadhash (uuid/v1)
            spinner (spin/create!)]
        (let [source (str payloadhash)
              target (str tmp "/" "payload.tar.gz")]
          (spin/start! spinner)
          (spin/print (str "Starting deployment with ID " source "\n"))
          (spin/print "Preparing files...")
          (fs/copy-dir _payload source)
          (spin/print "Done\n")
          (spin/print "Creating deployment payload...")
          (shell/sh "tar" "-zcvf" target source)
          (spin/print "Done\n")
          (spin/print "Deploying...")
          (client/post server
            {:multipart [{:name "payload" :content (clojure.java.io/file target)}]})
          (spin/print "Done\n")
          (spin/print "Removing temporary files...")
          (fs/delete-dir source)
          (spin/print "Done\n")
          (spin/stop! spinner)
          (println (str "\nDeployment " source " deployed successfully"))
          ))
      (println "Invalid parameters run: hermes deploy -h for help")))
      

(deftask login
	"Authenticates the current user" 
  	[ s server SERVER str "target deployment server"]
	(println "Coming soon"))