package com.troblecodings.invisiblelights;

import java.util.List;

import com.troblecodings.invisiblelights.blocks.BlockCustomLight;
import com.troblecodings.invisiblelights.blocks.BlockCustomState;
import com.troblecodings.invisiblelights.init.ILInit;

public final class BlockLists {

    private List<String> stateless;
    private List<String> statebased;

    public void registerInto() {
        stateless.forEach(name -> ILInit.register(name, props -> new BlockCustomLight(props, 0)));
        statebased.forEach(name -> ILInit.register(name, props -> new BlockCustomState(props, 0)));
    }
}
