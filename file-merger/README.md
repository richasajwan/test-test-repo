# file-merger

Quarkus app that reads three CSV files and merges them into one CSV in `results/`.

## Run

```bash
cd file-merger
mvn package
java -jar target/quarkus-app/quarkus-run.jar
```

Output: `results/merged.csv`, plus a per-file row count printed to the console.

## Configure

`src/main/resources/application.properties`:

| Key | Default | Meaning |
|---|---|---|
| `merge.input-files` | `input/customers.csv,input/orders.csv,input/products.csv` | Exactly three CSV inputs, in order |
| `merge.output-dir` | `results` | Output folder (created if missing) |
| `merge.output-file` | `merged.csv` | Output file name |

Override at runtime, e.g. `java -Dmerge.input-files=a.csv,b.csv,c.csv -jar ...`.

## Merge rules

- Output columns = `source_file` followed by the union of all input headers (first-seen order).
- Each row records its origin in `source_file`; cells not present in that file are empty.
- Rows with the wrong number of cells fail the merge.
- Values are split on commas; quoted fields containing commas are not supported.

## Test

```bash
mvn test
```
