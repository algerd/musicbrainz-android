package org.musicbrainz.android.intent;

import android.content.Context;
import android.content.Intent;

import org.musicbrainz.android.activity.AboutActivity;
import org.musicbrainz.android.activity.AlbumSearchActivity;
import org.musicbrainz.android.activity.ArtistActivity;
import org.musicbrainz.android.activity.ArtistSearchActivity;
import org.musicbrainz.android.activity.BarcodeSearchActivity;
import org.musicbrainz.android.activity.FeedbackActivity;
import org.musicbrainz.android.activity.ImageActivity;
import org.musicbrainz.android.activity.LoginActivity;
import org.musicbrainz.android.activity.MainActivity;
import org.musicbrainz.android.activity.RecordingActivity;
import org.musicbrainz.android.activity.ReleaseActivity;
import org.musicbrainz.android.activity.SettingsActivity;
import org.musicbrainz.android.activity.TagActivity;
import org.musicbrainz.android.activity.TrackSearchActivity;
import org.musicbrainz.android.activity.UserActivity;
import org.musicbrainz.android.api.site.TagServiceInterface;

import static org.musicbrainz.android.activity.UserActivity.DEFAULT_USER_NAV_VIEW;

public class ActivityFactory {

    public static void startTagActivity(Context context, String tag, TagServiceInterface.TagType tagType) {
        Intent intent = new Intent(context, TagActivity.class);
        intent.putExtra(TagActivity.MB_TAG, tag);
        intent.putExtra(TagActivity.TAG_TYPE, tagType.toString());
        context.startActivity(intent);
    }

    public static void startUserActivity(Context context, String username) {
        startUserActivity(context, username, DEFAULT_USER_NAV_VIEW);
    }

    public static void startUserActivity(Context context, String username, int userNavigationView) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserActivity.USERNAME, username);
        intent.putExtra(UserActivity.NAV_VIEW, userNavigationView);
        context.startActivity(intent);
    }

    public static void startArtistSearchActivity(Context context, String artist) {
        Intent intent = new Intent(context, ArtistSearchActivity.class);
        intent.putExtra(ArtistSearchActivity.QUERY, artist);
        context.startActivity(intent);
    }

    public static void startAlbumSearchActivity(Context context, String artist, String album) {
        Intent intent = new Intent(context, AlbumSearchActivity.class);
        intent.putExtra(AlbumSearchActivity.ARTIST_SEARCH, artist);
        intent.putExtra(AlbumSearchActivity.ALBUM_SEARCH, album);
        context.startActivity(intent);
    }

    public static void startTrackSearchActivity(Context context, String artist, String album, String track) {
        Intent intent = new Intent(context, TrackSearchActivity.class);
        intent.putExtra(TrackSearchActivity.ARTIST_SEARCH, artist);
        intent.putExtra(TrackSearchActivity.ALBUM_SEARCH, album);
        intent.putExtra(TrackSearchActivity.TRACK_SEARCH, track);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void startSettingsActivity(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void startBarcodeSearchActivity(Context context, String barcode) {
        Intent intent = new Intent(context, BarcodeSearchActivity.class);
        intent.putExtra(BarcodeSearchActivity.BARCODE, barcode);
        context.startActivity(intent);
    }

    public static void startImageActivity(Context context, String imageUrl) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ImageActivity.IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    public static void startFeedbackActivity(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    public static void startAboutActivity(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    public static void startArtistActivity(Context context, String artistMbid) {
        startArtistActivity(context, artistMbid, ArtistActivity.DEFAULT_ARTIST_NAV_VIEW);
    }

    public static void startArtistActivity(Context context, String artistMbid, int navView) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra(ArtistActivity.ARTIST_MBID, artistMbid);
        intent.putExtra(ArtistActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

    public static void startReleaseActivity(Context context, String releaseMbid) {
        startReleaseActivity(context, releaseMbid, ReleaseActivity.DEFAULT_RELEASE_NAV_VIEW);
    }

    public static void startReleaseActivity(Context context, String releaseMbid, int navView) {
        Intent intent = new Intent(context, ReleaseActivity.class);
        intent.putExtra(ReleaseActivity.RELEASE_MBID, releaseMbid);
        intent.putExtra(ReleaseActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

    public static void startRecordingActivity(Context context, String recordingMbid) {
        startRecordingActivity(context, recordingMbid, RecordingActivity.DEFAULT_RECORDING_NAV_VIEW);
    }

    public static void startRecordingActivity(Context context, String recordingMbid, int navView) {
        Intent intent = new Intent(context, RecordingActivity.class);
        intent.putExtra(RecordingActivity.RECORDING_MBID, recordingMbid);
        intent.putExtra(RecordingActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

}
