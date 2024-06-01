package com.jnlqcli.commands.aiclient;

import io.micronaut.http.client.annotation.Client;

@Client("${claude.ai.api}")
public interface AiClient {


}
