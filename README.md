# branch-api-client

A client library for accessing [Branch APIs](https://help.branch.io/developers-hub).

## [Aggregate Exports](https://help.branch.io/developers-hub/docs/aggregate-exports)

## [Cohort Exports](https://help.branch.io/developers-hub/docs/cohort-exports)

## [Custom Exports](https://help.branch.io/developers-hub/docs/custom-exports)

### Creating a client instance

```java
var appId = getAppId();
var accessCode = getAccessCode();
var client = CustomExportClient.builder()
        .appId(appId)
        .accessToken(accessCode)
        .connectTimeout(Duration.ofSeconds(1L))
        .timeout(Duration.ofSeconds(2L))
        .build();
```

### Requesting an export
```java
var request = ExportCreationRequest.builder()
        .reportType(reportType)
        .startDateLocal(...) // local date time
        .endDateLocal(...) // local date time
        .timezone(...) // defaults to systemDefault(0
        .fields(Set.of("timestamp", "name"))
        .limit(4)
        .build();
var response = client.requestExport(request).get();
if (response.hasErrors(v -> log.error("error: {}", v))) {
    // failed to request!
    return;
}
```

### Checking the status of an export job.

Now, periodically, and for a limited count, check the status of the export job.

```java
var status = client.checkStatus(response).get();
if (status.isCompleted()) { // "status": "completed"
    // Now you can read the log.
}
```

### Reading an exported log

```java
try (var input = client.readExported(status).get()) {
    // Import the log
}
```

## [Daily Exports](https://help.branch.io/developers-hub/docs/daily-exports)

## [Cost Data Query API](https://help.branch.io/developers-hub/docs/cost-data-query-api)

## [Query API](https://help.branch.io/developers-hub/docs/query-api)

## [Deep Linking API](https://help.branch.io/developers-hub/docs/deep-linking-api)

## [Data Subject Request API](https://help.branch.io/developers-hub/docs/data-subject-request-api)
