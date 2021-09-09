# Adidas challenge

## Setup
* Clone repository.
* Sync the dependencies.
* Change the `API_BASE_URL` value inside the `build.gradle` of the app module to the IP address of the server.
* Run the application.
> If the app is running on an emulator and the server is running on the same computer, leave the IP address as is (`http://10.0.2.2`)

## Application usage
* The app will start by presenting the list of products. A search button on the right corner of the navigation bar will allow the user to perform search based on the name and description.
* Clicking on a product, the user will navigate the a detail screen of the product. Besides showing all the details and a larger image of the product, this screen will also display the list of its reviews. An overall rating value and the numbers of the reviews are also displayed.
* Each review in the list will have the rating value presented as stars, and a message.
* At this point, the user will have the option to add a new review to the product by clicking on the plus button of the review list.
* To add a review, a popup will be displayed, with a star rating bar and a text aread. Clicking on the `Add review` button will add the review to the list.
* If the app was not able to add the review due to server unreachability, the user will see a popup explaining that the server is stored locally, and will be synced as soon as possible.
* As soon as the user have internet connection again, the application will add a notification with a indefinite loader telling the user that the app is scanning for pending reviews in the background.
* If any pending review was found, the notification will display a progress bar with the number of successfuly synced reviews.
* In case of server unreachability, the app will display a bottom view telling the user that the app is running offline. Clicking that view will show a popup further explaining the situation to the user, and giving him the option to attempt a refresh. (This is useful in case server was down and truned back on)

## Behind the scenes.
* On the first screen the app will try to reach the server to fetch the list of products.
  * In case of unreachability (internet issue or server down), the app will check if there is any data stored in the local database and display it if any. Otherwise the screen will display an empty state message.
  * In case of server error, the app wil log the error to Crashlytics, then it will check if there is any data stored in the local database and display it if any. Otherwise the screen will display an error message
  * In case of successful connection, the app will update the database with the newly fetched products, and then return the data from the database.
* Search filtration will always be performed on the list of products stored in the database as API doesn't give a search option.
* When navigating the second screen (product details), the same behavior (already described for the products list) will occur.
* Fetching the reviews also uses the same logic. if therer isn't any review and empty data state will be displayed.
* When adding a review, the app will always store it to the local database first with its sync state attribute set to `PENDING` and the starts a background service to sync the added revie with API.
  * In case of server unreachability the background service will stop itself without performing any action.
  * Wheneve we have an internet connection again, and for each app launch, the background service will be started. Where it will scan the database for pending reviews, and if any sync them with the API, and update their sync state to `SYNCED` after successful syncing.
* The app is constantly listening for network changes, and will notify observers about it.
* The app uses Firebase Crashlytics to monitor application health. I can grant you access if needed.

## CI/CD
The app is using Github Actions as an automation engine.

### Debug pipeline
* On push or pull request merge to the [master](https://github.com/seif-challenges/Adidas/tree/master) branch, the pipeline defined in [`debug-workflow.yml`](https://github.com/seif-challenges/Adidas/blob/master/.github/workflows/debug-workflow.yml) starts.
* The pipeline will start by checking out the project and setting up the JDK environement.
* Later the pipeline will build the project using `assembleDebug` commange, run the `ktlint` Kotlin linter and run the unit tests.
* If all actions succeds, the pipeline will sign the generated APK using [debug_key.jks](https://github.com/seif-challenges/Adidas/blob/master/debug_key.jks) and the credentials defined in the repo secrets.
* If the commit title contains `[norelease]` the pipeline will stop at this point.
* Otherwise, the pipeline will create a release with default version scheme set to `minor` having `debug-` as prefix. For more version naming options check [Release on puch action](https://github.com/rymndhng/release-on-push-action).
* The pipeline will then upload the generated signed debug APK as an asset of the created release, that can be visited/downloaded from [here](https://github.com/seif-challenges/Adidas/releases/latest).

### Production pipeline
* On push or pull request merge to the [production](https://github.com/seif-challenges/Adidas/tree/production) branch, the pipeline defined in [`production-workflow.yml`](https://github.com/seif-challenges/Adidas/blob/master/.github/workflows/production-workflow.yml) starts.
* The pipeline will start by checking out the project and setting up the JDK environement.
* Later the pipeline will build the project using `assambleProduction` command, run the `ktlint` Kotlin linter and run the unit tests.
* If all actions succeds, the pipeline will sign the generated APK using [production_key.jks](https://github.com/seif-challenges/Adidas/blob/master/production_key.jks) and the credentials defined in the repo secrets.
* If the commit title contains `[norelease]` the pipeline will stop at this point.
* Otherwise, the pipeline will create a release with default version scheme set to `minor` having `prod-` as prefix. For more version naming options check [Release on puch action](https://github.com/rymndhng/release-on-push-action).
* The pipeline will then upload the generated signed production APK as an asset of the created release, that can be visited/downloaded from [here](https://github.com/seif-challenges/Adidas/releases/latest).
