package com.github.randoapp.api.listeners;

import com.github.randoapp.db.model.Rando;

public interface UploadRandoListener {

    void onUpload(Rando rando);
}
