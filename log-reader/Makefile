default: native-image

check-graal:
	test -n "${GRAALVM_HOME}" && test -x ${GRAALVM_HOME}/bin/java || { echo "Please set GRAALVM_HOME to a GraalVM installation"; exit 1; }

reflection-analysis: check-graal
	rm -rf ./resources/META-INF
	mkdir -p ./resources/META-INF/native-image
	lein clean
	lein uberjar
	cat data/testlog.log | ${GRAALVM_HOME}/bin/java -agentlib:native-image-agent=config-output-dir=./resources/META-INF/native-image/ -jar target/uberjar/log-reader-*-standalone.jar >/dev/null

native-image: reflection-analysis
	lein native-image

.PHONY: default check-graal reflection-analysis native-image
