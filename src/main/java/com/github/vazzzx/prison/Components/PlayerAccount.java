package com.github.vazzzx.prison.Components;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class PlayerAccount {

    private final String playerName;
    private boolean blocked;
    private int sentence;
    private int remainBlocks;



}
