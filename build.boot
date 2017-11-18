(def project 'mr.pointy)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"src" "test"}
          :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "1.8.0"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [org.clojure/data.xml "0.2.0-alpha3"]
                            
                            [it.frbracch/boot-marginalia "0.1.3-1"]])

(task-options!
 pom {:project     project
      :version     version
      :description "XML project"
      :url         "https://github.com/osfameron/"
      :scm         {:url "https://github.com/osfameron/"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 jar {:main        'mr.pointy
      :file        (str "mr-pointy-" version "-standalone.jar")})

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (pom) (uber) (jar) (target :dir dir))))

(require '[adzerk.boot-test :refer [test]])
(require '[it.frbracch.boot-marginalia :refer [marginalia]])
