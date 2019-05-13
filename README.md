# Migrate Plugin

This is a gradle plugin to migrate java key store files (JKS), to PKCS12 format.
In addition to that, plugin allows users to generate new public private key pairs in a PKCS12 file,
Resetting password of a JKS file and showing the key aliases in a JKS file.

Plugin defines following tasks

- resetpassword
- migrate
- showkeys
- generatekey

Each task requires a configuration block (except the showkeys task, that uses the migrate tasks configuration)

## resetpassword Task

Resets password of a JKS file, one thing is keys should have the same password with the store file, otherwise reading of the
key will fail. with Key Retrival Failed exception. Defines following extension

```
resetpassword {
  path = 'relative path of the jks file'
  password = 'password that you want the resulting jks file to have'
  
}
```

to execute the task just run 

gradle resetpassword

## migrate Task

Migrates a JKS file to PKCS12 file (sets the extension to p12). Again keys required to have the same password
as the jks store. Defines the following extension

```
migrate {
   path = 'relative path of the jks file'
   password = 'keystore password of the jks file'
}

```

to execute the task just run

gradle migrate

task will migrate jks file to PKCS12 format with a p12 file extension.

## showkeys

shows available key aliases in a JKS file, used with migrate task, to verify later all key pairs are migrated, does not 
define an extension (uses migrate tasks extension)

to execute just run

gradle showkeys

will output the key aliases in jks file

## generatekey 

Generates a new key with 'key-id-x' alias. where x is a number. If during generation task finds aliases with key-id-3 it will 
generate 'key-id-4'. Generated public private key will be RSAwithSHA256 key pair with 2048 bit length. Defines following extension:

```
generatekey {
  validityInDays = 3650
  issuer = "CN=TestOrganization, O=Test A.S., L=Istanbul, C=TR"
  relativePath = "path to pkcs12 keystore file(s)"
  pkcs12File = ".p12"
  password = "superupersecret"
}
```

to execute the task just run

gradle generatekey







