package com.bgautoradio.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RadioStationDao_Impl implements RadioStationDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfSetFavorite;

  private final EntityUpsertionAdapter<RadioStationEntity> __upsertionAdapterOfRadioStationEntity;

  public RadioStationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfSetFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE radio_stations SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfRadioStationEntity = new EntityUpsertionAdapter<RadioStationEntity>(new EntityInsertionAdapter<RadioStationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `radio_stations` (`id`,`name`,`streamUrl`,`logoUrl`,`country`,`city`,`category`,`tags`,`websiteUrl`,`bitrate`,`codec`,`isFavorite`,`isVerified`,`lastChecked`,`source`,`sortOrder`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RadioStationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getStreamUrl());
        if (entity.getLogoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLogoUrl());
        }
        statement.bindString(5, entity.getCountry());
        if (entity.getCity() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCity());
        }
        statement.bindString(7, entity.getCategory());
        statement.bindString(8, entity.getTags());
        if (entity.getWebsiteUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getWebsiteUrl());
        }
        if (entity.getBitrate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getBitrate());
        }
        if (entity.getCodec() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCodec());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.isVerified() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        if (entity.getLastChecked() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getLastChecked());
        }
        if (entity.getSource() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getSource());
        }
        statement.bindLong(16, entity.getSortOrder());
      }
    }, new EntityDeletionOrUpdateAdapter<RadioStationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `radio_stations` SET `id` = ?,`name` = ?,`streamUrl` = ?,`logoUrl` = ?,`country` = ?,`city` = ?,`category` = ?,`tags` = ?,`websiteUrl` = ?,`bitrate` = ?,`codec` = ?,`isFavorite` = ?,`isVerified` = ?,`lastChecked` = ?,`source` = ?,`sortOrder` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RadioStationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getStreamUrl());
        if (entity.getLogoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLogoUrl());
        }
        statement.bindString(5, entity.getCountry());
        if (entity.getCity() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCity());
        }
        statement.bindString(7, entity.getCategory());
        statement.bindString(8, entity.getTags());
        if (entity.getWebsiteUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getWebsiteUrl());
        }
        if (entity.getBitrate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getBitrate());
        }
        if (entity.getCodec() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCodec());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.isVerified() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        if (entity.getLastChecked() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getLastChecked());
        }
        if (entity.getSource() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getSource());
        }
        statement.bindLong(16, entity.getSortOrder());
        statement.bindString(17, entity.getId());
      }
    });
  }

  @Override
  public Object setFavorite(final String id, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetFavorite.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetFavorite.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<RadioStationEntity> stations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfRadioStationEntity.upsert(stations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final RadioStationEntity station,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfRadioStationEntity.upsert(station);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RadioStationEntity>> observeAll() {
    final String _sql = "SELECT * FROM radio_stations ORDER BY sortOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"radio_stations"}, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RadioStationEntity>> observeFavorites() {
    final String _sql = "SELECT * FROM radio_stations WHERE isFavorite = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"radio_stations"}, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RadioStationEntity>> observeByCategory(final String category) {
    final String _sql = "SELECT * FROM radio_stations WHERE category = ? ORDER BY sortOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"radio_stations"}, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RadioStationEntity>> search(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM radio_stations\n"
            + "        WHERE name LIKE '%' || ? || '%'\n"
            + "           OR city LIKE '%' || ? || '%'\n"
            + "           OR tags LIKE '%' || ? || '%'\n"
            + "        ORDER BY sortOrder ASC, name ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"radio_stations"}, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final String id,
      final Continuation<? super RadioStationEntity> $completion) {
    final String _sql = "SELECT * FROM radio_stations WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RadioStationEntity>() {
      @Override
      @Nullable
      public RadioStationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final RadioStationEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM radio_stations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllFavoritesOnce(
      final Continuation<? super List<RadioStationEntity>> $completion) {
    final String _sql = "SELECT * FROM radio_stations WHERE isFavorite = 1 AND streamUrl != '' ORDER BY sortOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllStationsOnce(
      final Continuation<? super List<RadioStationEntity>> $completion) {
    final String _sql = "SELECT * FROM radio_stations WHERE streamUrl != '' ORDER BY sortOrder ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RadioStationEntity>>() {
      @Override
      @NonNull
      public List<RadioStationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStreamUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "streamUrl");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfWebsiteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "websiteUrl");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfCodec = CursorUtil.getColumnIndexOrThrow(_cursor, "codec");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "lastChecked");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<RadioStationEntity> _result = new ArrayList<RadioStationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RadioStationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStreamUrl;
            _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpWebsiteUrl;
            if (_cursor.isNull(_cursorIndexOfWebsiteUrl)) {
              _tmpWebsiteUrl = null;
            } else {
              _tmpWebsiteUrl = _cursor.getString(_cursorIndexOfWebsiteUrl);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final String _tmpCodec;
            if (_cursor.isNull(_cursorIndexOfCodec)) {
              _tmpCodec = null;
            } else {
              _tmpCodec = _cursor.getString(_cursorIndexOfCodec);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpIsVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp_1 != 0;
            final String _tmpLastChecked;
            if (_cursor.isNull(_cursorIndexOfLastChecked)) {
              _tmpLastChecked = null;
            } else {
              _tmpLastChecked = _cursor.getString(_cursorIndexOfLastChecked);
            }
            final String _tmpSource;
            if (_cursor.isNull(_cursorIndexOfSource)) {
              _tmpSource = null;
            } else {
              _tmpSource = _cursor.getString(_cursorIndexOfSource);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new RadioStationEntity(_tmpId,_tmpName,_tmpStreamUrl,_tmpLogoUrl,_tmpCountry,_tmpCity,_tmpCategory,_tmpTags,_tmpWebsiteUrl,_tmpBitrate,_tmpCodec,_tmpIsFavorite,_tmpIsVerified,_tmpLastChecked,_tmpSource,_tmpSortOrder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<String>> observeCities() {
    final String _sql = "SELECT DISTINCT city FROM radio_stations WHERE city IS NOT NULL ORDER BY city ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"radio_stations"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object pruneNonFavorites(final List<String> keepIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM radio_stations WHERE id NOT IN (");
        final int _inputSize = keepIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(") AND isFavorite = 0");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : keepIds) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
