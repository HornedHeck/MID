package ecdsa

import java.math.BigInteger
import java.security.MessageDigest

internal fun ByteArray.sha256() = BigInteger(1, MessageDigest.getInstance("SHA-256").digest(this))