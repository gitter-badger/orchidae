/**
 * Copyright (C) 2014 cherimojava (http://github.com/cherimojava/orchidae)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cherimojava.orchidae.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.google.common.base.Throwables;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * MongoDB configuration
 */
@Configuration
@Import({ cfgProduction.class, cfgTest.class })
public class cfgMongo {
	// TODO make this config available/should be possible to include it but not loading it

	@Autowired
	@Named("dbName")
	private String mongoDBName;

	@Autowired
	@Named("dbPath")
	private String dbpath;

	@Value("${log.path:./log}")
	String logPath;

	// TODO make mongo optional
	private MongodExecutable exe;

	File mongoStoragePath() {
		return new File(dbpath);
	}

	@Bean
	public File logPath() {
		return new File(logPath);
	}

	@Bean
	public EntityFactory entityFactory() throws UnknownHostException {
		return new EntityFactory(mongoDatabase());
	}

	@Bean
	Integer mongoPort() {
		// try {
		// return Network.getFreeServerPort();
		// } catch (IOException e) {
		return 27017;
		// }
	}

	@Bean
	Version version() {
		return Version.V2_6_1;
	}

	@Bean
	MongoDatabase mongoDatabase() throws UnknownHostException {
		return new MongoClient(new ServerAddress("localhost", mongoPort())).getDatabase(mongoDBName);
	}

	private IMongodConfig mongodConfig() throws IOException {
		return new MongodConfigBuilder().version(version()).net(new Net(mongoPort(), Network.localhostIsIPv6())).replication(
				new Storage(mongoStoragePath().toString(), null, 0)).timeout(new Timeout()).build();
	}

	private IRuntimeConfig runtimeConfig() {
		FixedPath path = new FixedPath("bin/");
		IStreamProcessor mongodOutput;
		IStreamProcessor mongodError;
		IStreamProcessor commandsOutput;
		try {
			mongodOutput = Processors.named("[mongod>]", new FileStreamProcessor(new File(logPath, "mongo.log")));

			mongodError = new FileStreamProcessor(new File(logPath, "mongo-err.log"));
			commandsOutput = Processors.named("[mongod>]", new FileStreamProcessor(new File(logPath, "mongo.log")));
		} catch (FileNotFoundException e) {
			throw Throwables.propagate(e);
		}
		return new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(
				new ProcessOutput(mongodOutput, mongodError, commandsOutput)).artifactStore(
				new ArtifactStoreBuilder().executableNaming(new UserTempNaming()).tempDir(path).download(
						new DownloadConfigBuilder().defaultsForCommand(Command.MongoD).artifactStorePath(path))).build();
	}

	@PostConstruct
	public void setup() {
		try {
			MongodStarter runtime = MongodStarter.getInstance(runtimeConfig());
			exe = runtime.prepare(mongodConfig());
			exe.start();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@PreDestroy
	public void teardown() {
		exe.stop();
	}

	public class FileStreamProcessor implements IStreamProcessor {

		private FileOutputStream outputStream;

		public FileStreamProcessor(File file) throws FileNotFoundException {
			file.getParentFile().mkdirs();
			outputStream = new FileOutputStream(file);
		}

		@Override
		public void process(String block) {
			try {
				outputStream.write(block.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcessed() {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
