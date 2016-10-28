package com.github.randoapp.api.listeners;

import com.github.randoapp.db.model.Rando;

public interface UploadRandoListener {

    public void onUpload(Rando rando);
}
