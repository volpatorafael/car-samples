/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.libraries.car.app.samples.showcase.common;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.model.DistanceSpan;
import com.google.android.libraries.car.app.model.ForegroundCarColorSpan;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.LatLng;
import com.google.android.libraries.car.app.model.Metadata;
import com.google.android.libraries.car.app.model.Place;
import com.google.android.libraries.car.app.model.PlaceMarker;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.samples.showcase.R;
import java.util.ArrayList;
import java.util.List;

/** Provides sample place data used in the demos. */
public class SamplePlaces {
  /** The location to use as an anchor for calculating distances. */
  private final Location mAnchorLocation;

  private final List<PlaceInfo> mPlaces;
  private final Screen mDemoScreen;

  public static SamplePlaces create(Screen demoScreen) {
    return new SamplePlaces(demoScreen);
  }

  /** Retusn the {@link ItemList} of the sample places. */
  public ItemList getPlaceList() {
    ItemList.Builder listBuilder = ItemList.builder();

    for (PlaceInfo place : mPlaces) {
      // Build a description string that includes the required distance span.
      int distanceKm = getDistanceFromCurrentLocation(place.location) / 1000;
      SpannableString description = new SpannableString("   \u00b7 " + place.description);
      description.setSpan(
          DistanceSpan.create(Distance.create(distanceKm, Distance.UNIT_KILOMETERS)),
          0,
          1,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      description.setSpan(
          ForegroundCarColorSpan.create(CarColor.BLUE), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      // Add the row for this place to the list.
      listBuilder.addItem(
          Row.builder()
              .setTitle(place.title)
              .addText(description)
              .setOnClickListener(() -> onClickPlace(place))
              .setMetadata(
                  Metadata.ofPlace(
                      Place.builder(LatLng.create(place.location)).setMarker(place.marker).build()))
              .build());
    }

    return listBuilder.build();
  }

  /** Returns the distance in meters of the {@code location} from the current location. */
  private int getDistanceFromCurrentLocation(Location location) {
    return (int) mAnchorLocation.distanceTo(location);
  }

  private void onClickPlace(PlaceInfo place) {
    mDemoScreen
        .getScreenManager()
        .push(PlaceDetailsScreen.create(mDemoScreen.getCarContext(), place));
  }

  private SamplePlaces(Screen demoScreen) {
    mDemoScreen = demoScreen;

    CarContext carContext = demoScreen.getCarContext();

    mAnchorLocation = new Location("ShowcaseDemo");
    mAnchorLocation.setLatitude(47.6204588);
    mAnchorLocation.setLongitude(-122.1918818);

    mPlaces = getSamplePlaces(carContext);
  }

  /**
   * Returns the list of sample places.
   *
   * <p>We use a few Google locations around the Seattle area, using different types of markers to
   * showcase those options. The "description" field of each place describes the type of marker
   * itself.
   */
  private static List<PlaceInfo> getSamplePlaces(CarContext carContext) {
    List<PlaceInfo> places = new ArrayList<>();

    Location location1 = new Location(SamplePlaces.class.getSimpleName());
    location1.setLatitude(47.6696482);
    location1.setLongitude(-122.19950278);
    places.add(
        new PlaceInfo(
            "Google Kirkland",
            "747 6th St South, Kirkland, WA 98033",
            "Tinted resource vector",
            "KIR",
            "+14257395600",
            location1,
            PlaceMarker.builder()
                .setIcon(
                    CarIcon.builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_commute_24px))
                        .setTint(CarColor.BLUE)
                        .build(),
                    PlaceMarker.TYPE_ICON)
                .build()));

    Location location2 = new Location(SamplePlaces.class.getSimpleName());
    location2.setLatitude(47.6204588);
    location2.setLongitude(-122.1918818);
    places.add(
        new PlaceInfo(
            "Google Bellevue",
            "1120 112th Ave NE, Bellevue, WA 98004",
            "Image resource bitmap",
            "BVE",
            "+14252301301",
            location2,
            PlaceMarker.builder()
                .setIcon(
                    CarIcon.builder(IconCompat.createWithResource(carContext, R.drawable.ic_hi))
                        .build(),
                    PlaceMarker.TYPE_IMAGE)
                .build()));

    Location location3 = new Location(SamplePlaces.class.getSimpleName());
    location3.setLatitude(47.625567);
    location3.setLongitude(-122.336427);
    places.add(
        new PlaceInfo(
            "Google South Lake Union",
            "1021 Valley St, Seattle, WA 98109",
            "Colored text marker",
            "SLU",
            "+12065311800",
            location3,
            PlaceMarker.builder().setLabel("SLU").setColor(CarColor.RED).build()));

    Location location4 = new Location(SamplePlaces.class.getSimpleName());
    location4.setLatitude(47.6490374);
    location4.setLongitude(-122.3527127);
    places.add(
        new PlaceInfo(
            "Google Seattle",
            "601 N 34th St, Seattle, WA 98103",
            "Image bitmap",
            "SEA",
            "+12068761800",
            location4,
            PlaceMarker.builder()
                .setIcon(
                    CarIcon.of(
                        IconCompat.createWithBitmap(
                            BitmapFactory.decodeResource(
                                carContext.getResources(), R.drawable.banana))),
                    PlaceMarker.TYPE_IMAGE)
                .build()));

    return places;
  }
}