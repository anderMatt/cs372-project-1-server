Execution
===

```bash
$ mvn clean install
$ mvn package
$ mvn:exec java -Dexec.mainClass="github.andermatt.Main" -Dexec.args="{PORT}"
```